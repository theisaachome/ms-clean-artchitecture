package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.entity.AggregateRoot;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import com.food.ordering.system.valueobject.*;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    private  final CustomerId customerId;
    private  final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;
    private  TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    public  void initializeOrder(){
        setId(new OrderId(UUID.randomUUID()));
        trackingId =  new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }
    public  void validateOrder(){
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateInitialOrder() {
        if(orderStatus !=null || getId()!=null){
            throw new OrderDomainException("Order is not in correct state for initialization!");
        }
    }
    private  void validateTotalPrice(){
        if(price==null || !price.isGreaterThanZero()){
            throw new OrderDomainException("Total Price must be Greater than Zero!");
        }
    }
    private  void validateItemsPrice(){
        Money orderItemsPrice = items.stream().map(ot -> {
            validateItemPrice(ot);
            return ot.getSubtotal();
        }).reduce(Money.ZERO, Money::add);
        if(!price.equals(orderItemsPrice)){
            throw new OrderDomainException(String.format("Total price : %f is not equal to Order items total : %f .",price.getAmount(),orderItemsPrice.getAmount()));
        }
    }

    // product.getId().getValue() to be update.
    private  void validateItemPrice(OrderItem orderItem){
        if(orderItem.isPriceValid()){
            throw new OrderDomainException(String.format("Order Item price: %f is not valid for Product %s",orderItem.getPrice().getAmount(),orderItem.getProduct().getId()));
        }
    }

    private  void initializeOrderItems(){
        long itemId =1;
        for (OrderItem orderItem:items){
            orderItem.initializeOrderItem(super.getId(),new OrderItemId(itemId++));
        }

    }

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }


    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
