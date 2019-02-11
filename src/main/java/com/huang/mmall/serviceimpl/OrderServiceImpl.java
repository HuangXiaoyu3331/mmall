package com.huang.mmall.serviceimpl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.dozermapper.core.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.huang.mmall.bean.pojo.*;
import com.huang.mmall.bean.vo.OrderItemVo;
import com.huang.mmall.bean.vo.OrderProductVo;
import com.huang.mmall.bean.vo.OrderVo;
import com.huang.mmall.bean.vo.ShippingVo;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.dao.OrderMapper;
import com.huang.mmall.service.*;
import com.huang.mmall.util.BigDecimalUtil;
import com.huang.mmall.util.DateTimeUtil;
import com.huang.mmall.util.FtpUtil;
import com.huang.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * OrderServiceImpl class
 *
 * @author hxy
 * @date 2019/1/29
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private PayInfoService payInfoService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private Mapper mapper;
    @Autowired
    private ShippingService shippingService;

    @Override
    public ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId) {
        //从购物车中获取已经勾选的产品
        ServerResponse<List<Cart>> cartResponse = cartService.getCheckedCartByUserId(userId);
        if (!cartResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(cartResponse.getMsg());
        } else {
            List<Cart> cartList = cartResponse.getData();
            ServerResponse<List<OrderItem>> orderItemResponse = getCartOrderItem(userId, cartList);
            if (!orderItemResponse.isSuccess()) {
                return ServerResponse.createByErrorMessage(orderItemResponse.getMsg());
            }
            List<OrderItem> orderItemList = orderItemResponse.getData();
            //计算这个订单的总价
            BigDecimal payment = getOrderTotalPrice(orderItemList);
            //生成订单
            Order order = assembleOrder(userId, shippingId, payment);
            if (order == null) {
                return ServerResponse.createByErrorMessage("生成订单错误");
            }
            orderItemList.forEach(orderItem -> {
                orderItem.setOrderNo(order.getOrderNo());
            });
            //mybatis批量插入
            ServerResponse batchResponse = orderItemService.batchInsert(orderItemList);
            if (!batchResponse.isSuccess()) {
                return ServerResponse.createByErrorMessage(batchResponse.getMsg());
            }
            //生成成功，减少产品的库存
            productService.reduceProductStock(orderItemList);
            //清空购物车
            cartService.cleanCart(cartList);
            //返回订单明细给前端
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
    }

    @Override
    public ServerResponse<String> cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户没有该订单");
        }
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("该订单已付款，无法取消订单");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int resultCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByError();
        }
    }

    @Override
    public ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId) {
        OrderProductVo orderProductVo = new OrderProductVo();
        ServerResponse<List<Cart>> serverResponse = cartService.getCheckedCartByUserId(userId);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(serverResponse.getMsg());
        }
        List<Cart> cartList = serverResponse.getData();
        ServerResponse<List<OrderItem>> getCartOrderItemResponse = getCartOrderItem(userId, cartList);
        if (!getCartOrderItemResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(getCartOrderItemResponse.getMsg());
        }
        List<OrderItem> orderItemList = getCartOrderItemResponse.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        //初始化订单总价
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            OrderItemVo orderItemVo = mapper.map(orderItem, OrderItemVo.class);
            orderItemVoList.add(orderItemVo);
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        //校验购物车的数据，包括产品的状态和数量
        for (Cart cart : cartList) {
            ServerResponse<Product> serverResponse = productService.get(cart.getProductId());
            if (!serverResponse.isSuccess()) {
                return ServerResponse.createByErrorMessage("找不到该商品");
            }
            Product product = serverResponse.getData();
            OrderItem orderItem = new OrderItem();
            //校验库存
            if (cart.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("商品" + product.getName() + "库存不足");
            }
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), cart.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    @Override
    public ServerResponse<PageInfo> getOrderList(Integer userId, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("没有找到该订单");
        }
        ServerResponse<List<OrderItem>> orderItemResponse = orderItemService.getByOrderNoUserId(orderNo, userId);
        if (!orderItemResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(orderItemResponse.getMsg());
        }
        OrderVo orderVo = assembleOrderVo(order, orderItemResponse.getData());
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse<Map<String, String>> pay(Integer userId, Long orderNo, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();
        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = String.format("mmall商城扫码支付，订单号:%s", order.getOrderNo().toString());
        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();
        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";
        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = StringUtils.EMPTY;
        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = String.format("订单:%s，购买商品共%s元", outTradeNo, totalAmount);
        // 商户操作员编号，添加此参数可以为商户操作员做销售统计(连锁店可用这个)
        String operatorId = "test_operator_id";
        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";
        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");
        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        ServerResponse<List<OrderItem>> orderItemResponse = orderItemService.getByOrderNoUserId(orderNo, userId);
        if (orderItemResponse.isSuccess()) {
            orderItemResponse.getData().forEach(item -> {
                // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
                GoodsDetail goods = GoodsDetail.newInstance(item.getProductId().toString(), item.getProductName(),
                        BigDecimalUtil.multiply(item.getCurrentUnitPrice().doubleValue(), 100d).longValue()
                        , item.getQuantity());
                goodsDetailList.add(goods);
            });
        } else {
            // 商品订单错误
            return ServerResponse.createByErrorMessage("下单失败！订单异常，该订单查不到商品");
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                //生成二维码，把二维码放到服务器上，返回给前端
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 二维码保存路径（path传过来最后是带“/”的，所以这里不用加）
                String qrPath = String.format("%sqr-%s.png", path, response.getOutTradeNo());
                // 二维码文件名
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File qrFile = new File(path, qrFileName);
                FtpUtil.uploadFile(Lists.newArrayList(qrFile));
                log.info("qrPath:{}", qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + qrFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    /**
     * 简单打印应答
     *
     * @param response
     */
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info("code:{}, msg:{}", response.getCode(), response.getMsg());
            if (org.apache.commons.lang.StringUtils.isNotEmpty(response.getSubCode())) {
                log.info("subCode:{}, subMsg:{}", response.getSubCode(),
                        response.getSubMsg());
            }
            log.info("body:{}", response.getBody());
        }
    }

    @Override
    public ServerResponse alipayCallback(Map<String, String> params) {
        //订单号
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        //支付宝交易号
        String tradeNo = params.get("trade_no");
        //交易状态
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("非mmall商城的订单，回调忽略");
        } else {
            if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
                return ServerResponse.createBySuccess("支付宝重复调用");
            }
            if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
                order.setPaymentTime(DateTimeUtil.str2Date(params.get("gmt_payment")));
                order.setStatus(Const.OrderStatusEnum.PAID.getCode());
                orderMapper.updateByPrimaryKeySelective(order);
            }
            PayInfo payInfo = new PayInfo();
            payInfo.setUserId(order.getUserId());
            payInfo.setOrderNo(order.getOrderNo());
            payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
            //支付宝交易号
            payInfo.setPlatformNumber(tradeNo);
            payInfo.setPlatformStatus(tradeStatus);
            payInfoService.save(payInfo);
            return ServerResponse.createBySuccess();
        }
    }

    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<PageInfo> manageList(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Order> orderList = orderMapper.selectAll();
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, null);
        PageInfo pageReslut = new PageInfo();
        pageReslut.setList(orderVoList);
        return ServerResponse.createBySuccess(pageReslut);
    }

    @Override
    public ServerResponse<OrderVo> manageDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = new OrderVo();
        if (order != null) {
            ServerResponse<List<OrderItem>> response = orderItemService.getByOrderNo(orderNo);
            if (response.isSuccess()) {
                List<OrderItem> orderItemList = response.getData();
                orderVo = assembleOrderVo(order, orderItemList);
            }
            return ServerResponse.createBySuccess(orderVo);
        }

        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        //为了以后模糊查询拓展，所以这里还是用分页
        PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
        if (order != null) {
            ServerResponse<List<OrderItem>> response = orderItemService.getByOrderNo(orderNo);
            if (response.isSuccess()) {
                List<OrderItem> orderItemList = response.getData();
                OrderVo orderVo = assembleOrderVo(order, orderItemList);
                pageResult.setList(Lists.newArrayList(orderVo));
            }
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<String> manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                int resultCount = orderMapper.updateByPrimaryKeySelective(order);
                if (resultCount > 0) {
                    return ServerResponse.createBySuccess("发货成功");
                } else {
                    return ServerResponse.createBySuccess("发货失败，请重试！");
                }
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    /**
     * 获取订单的总价
     *
     * @param orderItemList 订单明细list
     * @return
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem item : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), item.getTotalPrice().doubleValue());
        }
        return payment;
    }

    /**
     * 获取订单号
     * 订单号生成规则：时间戳 + 100以内的随机数
     * 这里由于程序的并发少，订单号生成规则可以这样简单的来
     * 如果是并发量特别大的系统，可以开一个定时任务跑一个订单池，把第二天需要用到的订单号放进一个订单池里，
     * 然后在需要创建订单的时候，直接从订单池里面取
     *
     * @return 订单号
     */
    private long generatorOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(100);
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        long orderNo = generatorOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        // 包邮
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        //发货时间等等不用set，在发货的时候再更新
        int resultCount = orderMapper.insert(order);
        if (resultCount > 0) {
            return order;
        } else {
            return null;
        }
    }

    /**
     * 组装orderItem的一个Vo
     *
     * @param order         订单order
     * @param orderItemList 订单详情item
     * @return
     */
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = mapper.map(order, OrderVo.class);
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getMsg());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getMsg());
        ServerResponse<Shipping> shippingServerResponse = shippingService.get(order.getShippingId());
        if (shippingServerResponse.isSuccess()) {
            Shipping shipping = shippingServerResponse.getData();
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(mapper.map(shipping, ShippingVo.class));
        }
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        orderItemList.forEach(orderItem -> {
            OrderItemVo orderItemVo = mapper.map(orderItem, OrderItemVo.class);
            orderItemVoList.add(orderItemVo);
        });
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        orderList.forEach(order -> {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null) {
                //管理员查询的时候，不需要传userId
                ServerResponse<List<OrderItem>> response = orderItemService.getByOrderNo(order.getOrderNo());
                if (response.isSuccess()) {
                    orderItemList = response.getData();
                }
            } else {
                ServerResponse<List<OrderItem>> response = orderItemService.getByOrderNoUserId(order.getOrderNo(), userId);
                if (response.isSuccess()) {
                    orderItemList = response.getData();
                }
            }
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        });
        return orderVoList;
    }
}
