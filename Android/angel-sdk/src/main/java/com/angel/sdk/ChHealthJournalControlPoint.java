/*
 * Copyright (c) 2015, Seraphim Sense Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *    endorse or promote products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.angel.sdk;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.angel.sdk.ChHealthJournalControlPoint.HealthJournalControlPointValue;

import java.util.Date;
import java.util.UUID;


/** http://angelsensor.com/protocols/seraphim-sense/health-journal-service/ */
public class ChHealthJournalControlPoint extends BleCharacteristic<HealthJournalControlPointValue> {
    public final static UUID CHARACTERISTIC_UUID = UUID.fromString("5ae61782-4a65-4202-a4da-db73406e38e8");

    public ChHealthJournalControlPoint(BluetoothGattCharacteristic gattCharacteristic,
                                BleDevice bleDevice) {
        super(CHARACTERISTIC_UUID, gattCharacteristic, bleDevice);
    }

    public ChHealthJournalControlPoint() {
        super(CHARACTERISTIC_UUID);
    }

    public static final byte OP_QUERY  = 1;
    public static final byte OP_DELETE = 2;
    public static final byte OP_ABORT  = 3;
    public static final byte OP_COUNT  = 4;

    public enum Opcode {
        Query ((byte) 1),
        Delete((byte) 2),
        Abort ((byte) 3),
        Count ((byte) 4);

        private byte Opcode;

        Opcode(byte Opcode) {
            this.Opcode = Opcode;
        }

        public byte getByte() { return Opcode; }
    }

    public enum Operator {
        All         ((byte) 0),
        First       ((byte) 1),
        Last        ((byte) 2),
        LessThan    ((byte) 3),
        GreaterThan ((byte) 4),
        Range       ((byte) 5);

        private byte Operator;

        Operator(byte Operator) {
            this.Operator = Operator;
        }

        public byte getByte() { return Operator; }
    }

    public void query(Operator operand, Date ... ops) {
        switch(operand) {
            case All:
            case First:
            case Last:
                assert(ops.length == 0);
                break;

            case LessThan:
            case GreaterThan:
                assert(ops.length == 1);
                break;

            case Range:
                assert(ops.length == 2);
                break;

            default:
                assert(false);
        }
        writeCommand(Opcode.Query.getByte(), operand.getByte(), ops);
    }
    public void delete(){}
    public void abort(){
    }
    public void count(){}

    private void writeCommand(byte opcode, byte operand, Date[] ops) {
        BluetoothGattCharacteristic c = getBaseGattCharacteristic();

        c.setValue(opcode, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        c.setValue(operand, BluetoothGattCharacteristic.FORMAT_UINT8, 1);
        int nextOffset = 2;
        for (Date op : ops) {
            c.setValue(op.getYear(),    BluetoothGattCharacteristic.FORMAT_UINT16, nextOffset + 0);
            c.setValue(op.getMonth(),   BluetoothGattCharacteristic.FORMAT_UINT8,  nextOffset + 2);
            c.setValue(op.getDay(),     BluetoothGattCharacteristic.FORMAT_UINT8,  nextOffset + 3);
            c.setValue(op.getHours(),   BluetoothGattCharacteristic.FORMAT_UINT8,  nextOffset + 4);
            c.setValue(op.getMinutes(), BluetoothGattCharacteristic.FORMAT_UINT8,  nextOffset + 5);
            c.setValue(op.getSeconds(), BluetoothGattCharacteristic.FORMAT_UINT8,  nextOffset + 6);
            nextOffset += 7;
        }
        getBleDevice().writeCharacteristic(c);
    }

    @Override
    protected HealthJournalControlPointValue processCharacteristicValue() {
        // This is a write-only characteristic
        return null;
    }

    public class HealthJournalControlPointValue {
    }
}


