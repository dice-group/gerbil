package org.aksw.gerbil.dataset.impl.mt;

import java.io.IOException;

public class EvalMetric {


        public static void main(String[] args){
                try {
                        Runtime.getRuntime().exec("bash result.sh ");
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
        }

