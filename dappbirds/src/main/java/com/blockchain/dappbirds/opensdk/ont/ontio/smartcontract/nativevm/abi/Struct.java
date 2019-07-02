package com.blockchain.dappbirds.opensdk.ont.ontio.smartcontract.nativevm.abi;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description:
 * @date 2018/6/10
 */
public class Struct {
    public List<Object> list = new ArrayList();
    public Struct(){

    }
    public Struct add(Object... objs){
        for(int i=0;i<objs.length;i++){
            list.add(objs[i]);
        }
        return this;
    }
}
