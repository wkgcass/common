package net.cassite.daf4j.stream;

import net.cassite.daf4j.DataAccess;
import net.cassite.daf4j.QueryParameter;
import net.cassite.daf4j.Where;

/**
 * Created by wkgcass on 15/10/28.
 */
public class StreamTestUtils {
        public static Object getEntity(QueryStreamBase<?, ?> stream) {
                return stream.entity;
        }

        public static Where getAndOr(QueryStreamBase<?, ?> stream) {
                return stream.andOr;
        }

        public static QueryParameter getParameter(QueryStreamBase<?, ?> stream) {
                return stream.parameter;
        }

        public static DataAccess getDataAccess(QueryStreamBase<?, ?> stream) {
                return stream.dataAccess;
        }
}
