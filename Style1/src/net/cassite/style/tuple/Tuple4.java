package net.cassite.style.tuple;

public class Tuple4<T1, T2, T3, T4> implements Tuple {
        public final T1 _1;
        public final T2 _2;
        public final T3 _3;
        public final T4 _4;

        public Tuple4(T1 _1, T2 _2, T3 _3, T4 _4) {
                this._1 = _1;
                this._2 = _2;
                this._3 = _3;
                this._4 = _4;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T $(int index) {
                if (index == 1)
                        return (T) _1;
                else if (index == 2)
                        return (T) _2;
                else if (index == 3)
                        return (T) _3;
                else if (index == 4)
                        return (T) _4;
                throw new IndexOutOfBoundsException(new Integer(index).toString());
        }

        @Override
        public int count() {
                return 4;
        }
}
