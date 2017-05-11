package datasync.datasync;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class CustomerEnricher implements AggregationStrategy {

    
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        oldExchange.getIn().setHeader("OrigId", oldExchange.getIn().getBody(String.class));
//        oldExchange.getOut().setBody(newExchange.getIn().getBody(String.class));
        oldExchange.getIn().setBody(newExchange.getIn().getBody(String.class));
        return oldExchange;
    }
}