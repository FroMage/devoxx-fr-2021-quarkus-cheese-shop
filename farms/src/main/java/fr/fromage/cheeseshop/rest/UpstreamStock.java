package fr.fromage.cheeseshop.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UpstreamStock {
    public List<OrderLine> stock = new ArrayList<>();
    public double price;
    public String origin;
    
    public static class OrderLine {
        public LocalDate availableDate;
        public int count;
    }

    public OrderLine now() {
        for(OrderLine line : stock) {
            if(line.availableDate == null) {
                return line;
            }
        }
        return null;
    }

    public OrderLine future() {
        for(OrderLine line : stock) {
            if(line.availableDate != null) {
                return line;
            }
        }
        return null;
    }
}
