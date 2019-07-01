/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.blockchain.dappbirds.opensdk.ont.ontio.smartcontract;

import com.blockchain.dappbirds.opensdk.ont.ontio.OntSdk;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.exception.SDKException;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Address;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Common;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Helper;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.payload.DeployCode;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.payload.InvokeCode;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.scripts.ScriptBuilder;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.scripts.ScriptOp;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.transaction.Attribute;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.transaction.Transaction;

import java.math.BigInteger;
import java.util.Random;

/**
 *
 */
public class Vm {
    private OntSdk sdk;
    private String contractAddress = null;
    public static  String NATIVE_INVOKE_NAME = "Ontology.Native.Invoke";
    public String getCodeAddress() {
        return contractAddress;
    }
    public void setCodeAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public Vm(OntSdk sdk) {
        this.sdk = sdk;
    }


    /**
     *
     * @param codeStr
     * @param needStorage
     * @param name
     * @param codeVersion
     * @param author
     * @param email
     * @param desp
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public DeployCode makeDeployCodeTransaction(String codeStr, boolean needStorage, String name, String codeVersion, String author, String email, String desp,String payer,long gaslimit,long gasprice) throws Exception {
        DeployCode tx = new DeployCode();
        if(payer != null){
            tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        }
        tx.attributes = new Attribute[0];
        tx.nonce = new Random().nextInt();
        tx.code = Helper.hexToBytes(codeStr);
        tx.version = codeVersion;
        tx.needStorage = needStorage;
        tx.name = name;
        tx.author = author;
        tx.email = email;
        tx.gasLimit = gaslimit;
        tx.gasPrice = gasprice;
        tx.description = desp;
        return tx;
    }
    //NEO makeInvokeCodeTransaction
    public InvokeCode makeInvokeCodeTransaction(String codeAddr,String method,byte[] params, String payer,long gaslimit,long gasprice) throws Exception {
        params = Helper.addBytes(params,new byte[]{0x67});
        params = Helper.addBytes(params, Address.parse(codeAddr).toArray());
        InvokeCode tx = new InvokeCode();
        tx.attributes = new Attribute[0];
        tx.nonce = new Random().nextInt();
        tx.code = params;
        tx.gasLimit = gaslimit;
        tx.gasPrice = gasprice;
        if(payer != null){
            tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        }
        return tx;
    }
    /**
     * Native makeInvokeCodeTransaction
     * @param params
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public InvokeCode makeInvokeCodeTransaction(byte[] params,String payer,long gaslimit,long gasprice) throws Exception {

        InvokeCode tx = new InvokeCode();
        tx.attributes = new Attribute[0];
        tx.nonce = new Random().nextInt();
        tx.code = params;
        tx.gasLimit = gaslimit;
        tx.gasPrice = gasprice;
        if(payer != null){
            tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        }
        return tx;
    }

    public Transaction buildNativeParams(Address codeAddr,String initMethod,byte[] args,String payer,long gaslimit,long gasprice) throws Exception {
        ScriptBuilder sb = new ScriptBuilder();
        if(args.length >0) {
            sb.add(args);
        }
        sb.emitPushByteArray(initMethod.getBytes());
        sb.emitPushByteArray(codeAddr.toArray());
        sb.emitPushInteger(BigInteger.valueOf(0));
        sb.add(ScriptOp.OP_SYSCALL);
        sb.emitPushByteArray(NATIVE_INVOKE_NAME.getBytes());
        Transaction tx = makeInvokeCodeTransaction(sb.toArray(),payer,gaslimit,gasprice);
        return tx;
    }
}
