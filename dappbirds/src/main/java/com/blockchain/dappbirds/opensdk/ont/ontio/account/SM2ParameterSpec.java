package com.blockchain.dappbirds.opensdk.ont.ontio.account;

import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.exception.SDKException;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.ErrorCode;

import org.spongycastle.util.Arrays;

import java.security.spec.AlgorithmParameterSpec;

/**
 * Parameter spec for SM2 ID parameter
 */
public class SM2ParameterSpec
    implements AlgorithmParameterSpec
{
    private byte[] id;

    /**
     * Base constructor.
     *
     * @param id the ID string associated with this usage of SM2.
     */
    public SM2ParameterSpec(byte[] id) throws SDKException {
        if (id == null){
            throw new SDKException(ErrorCode.ParamError);
        }
        this.id = Arrays.clone(id);
    }

    /**
     * Return the ID value.
     *
     * @return the ID string.
     */
    public byte[] getID()
    {
        return Arrays.clone(id);
    }
}
