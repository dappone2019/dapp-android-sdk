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

package com.blockchain.dappbirds.opensdk.ont.ontio.smartcontract.neovm.abi;


import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.exception.SDKException;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.ErrorCode;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Helper;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.scripts.ScriptBuilder;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.scripts.ScriptOp;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @date 2018/5/23
 */
public class BuildParams {
    public enum Type {
        ByteArrayType(0x00),
        BooleanType(0x01),
        IntegerType(0x02),
        InterfaceType(0x40),
        ArrayType(0x80),
        StructType(0x81),
        MapType(0x82);
        private byte type;

        private Type(int t) {
            this.type = (byte) t;
        }

        public byte getValue() {
            return type;
        }
    }

    /**
     * @param builder
     * @param list
     * @return
     */
    private static byte[] createCodeParamsScript(ScriptBuilder builder, List<Object> list) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    builder.emitPushByteArray((byte[]) val);
                } else if (val instanceof Boolean) {
                    builder.emitPushBool((Boolean) val);
                } else if (val instanceof Integer) {
                    builder.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((int) val)));
                } else if (val instanceof Long) {
                    builder.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((long) val)));
                } else if (val instanceof String) {
                    builder.emitPushByteArray(((String) val).getBytes());
                } else if (val instanceof Map) {
                    pushMap(builder, val);
                } else if (val instanceof Struct) {
                    pushStruct(builder, val);
                } else if (val instanceof List) {
                    List tmp = (List) val;
                    createCodeParamsScript(builder, tmp);
                    builder.emitPushInteger(new BigInteger(String.valueOf(tmp.size())));
                    builder.pushPack();
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toArray();
    }

    public static Object deserializeItem(byte[] mBytes) {
        ByteArrayInputStream ms = new ByteArrayInputStream(mBytes);
        BinaryReader reader = new BinaryReader(ms);
        return deserializeItem(reader);
    }

    public static Object deserializeItem(BinaryReader reader) {
        try {
            byte t = reader.readByte();
            if (t == Type.ByteArrayType.getValue()) {
                return reader.readVarBytes();
            } else if (t == Type.BooleanType.getValue()) {
                return reader.readBoolean();
            } else if (t == Type.IntegerType.getValue()) {
                long v = Helper.BigIntFromNeoBytes(reader.readVarBytes()).longValue();
                return v;
            } else if (t == Type.ArrayType.getValue() || t == Type.StructType.getValue()) {
                int count = (int) reader.readVarInt();
                List list = new ArrayList();
                for (int i = count; i > 0; i--) {
                    Object ele = deserializeItem(reader);
                    if (ele instanceof byte[]) {
                        ele = new String((byte[]) ele);
                    }
                    list.add(ele);
                    count--;
                }
                return list;
            } else if (t == Type.MapType.getValue()) {
                int count = (int) reader.readVarInt();
                Map map = new HashMap();
                for (int i = count; i > 0; i--) {
                    byte[] key = (byte[]) deserializeItem(reader);
                    Object value = deserializeItem(reader);
                    if (value instanceof byte[]) {
                        value = new String((byte[]) value);
                    }
                    map.put(new String(key), value);
                    count--;
                }
                return map;
            } else {
                throw new SDKException(ErrorCode.ParamError);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getStructBytes(Object val) {

        ScriptBuilder sb = null;
        try {
            sb = new ScriptBuilder();
            List list = ((Struct) val).list;
            sb.add(Type.StructType.getValue());
            sb.add(Helper.BigIntToNeoBytes(BigInteger.valueOf(list.size())));
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof byte[]) {
                    sb.add(Type.ByteArrayType.getValue());
                    sb.emitPushByteArray((byte[]) list.get(i));
                } else if (list.get(i) instanceof String) {
                    sb.add(Type.ByteArrayType.getValue());
                    sb.emitPushByteArray(((String) list.get(i)).getBytes());
                } else if (list.get(i) instanceof Integer) {
                    sb.add(Type.ByteArrayType.getValue());
                    sb.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((Integer) list.get(i))));
                } else if (list.get(i) instanceof Long) {
                    sb.add(Type.ByteArrayType.getValue());
                    sb.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((Long) list.get(i))));
                } else {
                    throw new SDKException(ErrorCode.ParamError);
                }
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
        return sb.toArray();
    }

    public static byte[] getMapBytes(Object val) {
        ScriptBuilder sb = null;
        try {
            sb = new ScriptBuilder();
            Map<String, Object> map = (Map) val;
            sb.add(Type.MapType.getValue());
            sb.add(Helper.BigIntToNeoBytes(BigInteger.valueOf(map.size())));
            for (Map.Entry e : map.entrySet()) {
                sb.add(Type.ByteArrayType.getValue());
                sb.emitPushByteArray(((String) e.getKey()).getBytes());
                if (e.getValue() instanceof byte[]) {
                    sb.add(Type.ByteArrayType.getValue());
                    sb.emitPushByteArray((byte[]) e.getValue());
                } else if (e.getValue() instanceof String) {
                    sb.add(Type.ByteArrayType.getValue());
                    sb.emitPushByteArray(((String) e.getValue()).getBytes());
                } else if (e.getValue() instanceof Integer) {
                    sb.add(Type.IntegerType.getValue());
                    sb.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((Integer) e.getValue())));
                } else if (e.getValue() instanceof Long) {
                    sb.add(Type.IntegerType.getValue());
                    sb.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((Long) e.getValue())));
                } else {
                    throw new SDKException(ErrorCode.ParamError);
                }
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
        return sb.toArray();
    }

    /**
     * @param list
     * @return
     */
    public static byte[] createCodeParamsScript(List<Object> list) {
        ScriptBuilder sb = new ScriptBuilder();
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    sb.emitPushByteArray((byte[]) val);
                } else if (val instanceof Boolean) {
                    sb.emitPushBool((Boolean) val);
                } else if (val instanceof Integer) {
                    sb.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((int) val)));
                } else if (val instanceof Long) {
                    sb.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((Long) val)));
                } else if (val instanceof BigInteger) {
                    sb.emitPushInteger((BigInteger) val);
                } else if (val instanceof Map) {
                    byte[] bys = getMapBytes(val);
                    sb.emitPushByteArray(bys);
                } else if (val instanceof Struct) {
                    byte[] bys = getStructBytes(val);
                    sb.emitPushByteArray(bys);
                } else if (val instanceof List) {
                    List tmp = (List) val;
                    createCodeParamsScript(sb, tmp);
                    sb.emitPushInteger(new BigInteger(String.valueOf(tmp.size())));
                    sb.pushPack();
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toArray();
    }

    public static void serializeStackItem(BinaryWriter bw, Object eValue) {
        try {
            if (eValue instanceof byte[]) {
                bw.writeByte(Type.ByteArrayType.getValue());
                bw.writeVarBytes((byte[]) eValue);
            } else if (eValue instanceof String) {
                bw.writeByte(Type.ByteArrayType.getValue());
                bw.writeVarBytes(((String) eValue).getBytes());
            } else if (eValue instanceof Boolean) {
                bw.writeByte(Type.BooleanType.getValue());
                bw.writeBoolean((Boolean) eValue);
            } else if (eValue instanceof Map) {
                bw.write(getMapBytes(eValue));
            } else if (eValue instanceof Struct) {
                bw.writeVarBytes(getStructBytes(eValue));
            } else if (eValue instanceof List) {
                List tmp = (List) eValue;
                bw.writeByte(Type.ArrayType.getValue());
                bw.writeVarInt(tmp.size());
                for (int i = 0; i < tmp.size(); i++) {
                    serializeStackItem(bw, tmp.get(i));
                }
            } else if (eValue instanceof Integer) {
                bw.writeByte(Type.IntegerType.getValue());
                bw.writeVarBytes(Helper.BigIntToNeoBytes(BigInteger.valueOf((Integer) eValue)));
            } else if (eValue instanceof Long) {
                bw.writeByte(Type.IntegerType.getValue());
                bw.writeVarBytes(Helper.BigIntToNeoBytes(BigInteger.valueOf((Long) eValue)));
            } else {
                throw new SDKException(ErrorCode.ParamError);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pushParam(ScriptBuilder sb, Object eValue) {
        try {
            if (eValue instanceof byte[]) {
                sb.emitPushByteArray((byte[]) eValue);
            } else if (eValue instanceof String) {
                sb.emitPushByteArray(((String) eValue).getBytes());
            } else if (eValue instanceof Boolean) {
                sb.emitPushBool((Boolean) eValue);
                sb.add(ScriptOp.OP_PUSH0);
                sb.add(ScriptOp.OP_BOOLOR);
            } else if (eValue instanceof Map) {
                pushMap(sb, eValue);
            } else if (eValue instanceof Struct) {
                List list = (List) ((Struct) eValue).list;
                for (int i = list.size() - 1; i >= 0; i--) {
                    Object val = list.get(i);
                    pushParam(sb, val);
                }
                sb.emitPushInteger(new BigInteger(String.valueOf(list.size())));
                sb.pushPack();
            } else if (eValue instanceof List) {
                List list = (List) eValue;
                for (int i = list.size() - 1; i >= 0; i--) {
                    Object val = list.get(i);
                    pushParam(sb, val);
                }
                sb.emitPushInteger(new BigInteger(String.valueOf(list.size())));
                sb.pushPack();
            } else if (eValue instanceof Integer) {
                sb.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((Integer) eValue)));
                sb.add(ScriptOp.OP_PUSH0);
                sb.add(ScriptOp.OP_ADD);
            } else if (eValue instanceof Long) {
                sb.emitPushByteArray(Helper.BigIntToNeoBytes(BigInteger.valueOf((Long) eValue)));
                sb.add(ScriptOp.OP_PUSH0);
                sb.add(ScriptOp.OP_ADD);
            } else {
                throw new SDKException(ErrorCode.ParamError);
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    public static void pushStruct(ScriptBuilder sb, Object val) {
        Struct struct = (Struct) val;
        sb.add(ScriptOp.OP_NEWSTRUCT);
        sb.add(ScriptOp.OP_TOALTSTACK);
        for (int i = 0; i < struct.list.size(); i++) {
            pushParam(sb, struct.list.get(i));
            sb.add(ScriptOp.OP_DUPFROMALTSTACK);
            sb.add(ScriptOp.OP_SWAP);
            sb.add(ScriptOp.OP_APPEND);
        }
        sb.add(ScriptOp.OP_FROMALTSTACK);
    }

    public static void pushMap(ScriptBuilder sb, Object val) {
        Map<String, Object> map = (Map) val;
        sb.add(ScriptOp.OP_NEWMAP);
        sb.add(ScriptOp.OP_TOALTSTACK);
        for (Map.Entry e : map.entrySet()) {
            sb.add(ScriptOp.OP_DUPFROMALTSTACK);
            pushParam(sb, e.getKey());
            pushParam(sb, e.getValue());
            sb.add(ScriptOp.OP_SETITEM);
        }
        sb.add(ScriptOp.OP_FROMALTSTACK);
    }

    /**
     * @param abiFunction
     * @return
     * @throws Exception
     */
    public static byte[] serializeAbiFunction(AbiFunction abiFunction) throws Exception {
        List list = new ArrayList<Object>();
        list.add(abiFunction.getName().getBytes());
        List tmp = new ArrayList<Object>();
        for (Parameter obj : abiFunction.getParameters()) {
            if ("ByteArray".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), byte[].class));
            } else if ("String".equals(obj.getType())) {
                tmp.add(obj.getValue());
            } else if ("Boolean".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), boolean.class));
            } else if ("Integer".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Long.class));
            } else if ("Array".equals(obj.getType())) {
                List l = JSON.parseObject(obj.getValue(), List.class);
                l = listConvert(l);
                tmp.add(l);
            } else if ("InteropInterface".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Object.class));
            } else if ("Void".equals(obj.getType())) {

            } else if ("Map".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Map.class));
            } else if ("Struct".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Struct.class));
            } else {
                throw new SDKException(ErrorCode.TypeError);
            }
        }
        if (list.size() > 0) {
            list.add(tmp);
        }
        byte[] params = createCodeParamsScript(list);
        return params;
    }

    private static List listConvert(List l) {
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i) instanceof String) {
                if (l.get(i) instanceof String) {
                    l.set(i, Base64.decode((String) l.get(i), Base64.NO_WRAP));
                }
            } else if (l.get(i) instanceof List) {
                l.set(i, listConvert((List) l.get(i)));
            }
        }
        return l;
    }
}