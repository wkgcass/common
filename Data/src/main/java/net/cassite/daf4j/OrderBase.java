package net.cassite.daf4j;

/**
 * 排序依据
 */
public class OrderBase {
        /**
         * 排序依据,定义在OrderType中
         *
         * @see OrderTypes
         */
        public final OrderTypes type;
        /**
         * 排序字段
         */
        public final IData<?> data;

        OrderBase(OrderTypes type, IData<?> data) {
                this.type = type;
                this.data = data;
        }

        @Override
        public boolean equals(Object o) {
                return (o instanceof OrderBase) && (this.type == ((OrderBase) o).type && this.data.equals(((OrderBase) o).data));
        }
}
