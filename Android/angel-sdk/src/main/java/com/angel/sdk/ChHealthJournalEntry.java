/*
 * Copyright (c) 2016, Daniel Green
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

import com.angel.sdk.ChHealthJournalEntry.HealthJournalEntryValue;

import java.util.Date;
import java.util.UUID;


/** http://angelsensor.com/protocols/seraphim-sense/health-journal-service/ */
public class ChHealthJournalEntry extends BleCharacteristic<HealthJournalEntryValue> {
    public final static UUID CHARACTERISTIC_UUID = UUID.fromString("8b713a94-070a-4743-a695-fc58cb3f236b");

    public ChHealthJournalEntry(BluetoothGattCharacteristic gattCharacteristic,
                                  BleDevice bleDevice) {
        super(CHARACTERISTIC_UUID, gattCharacteristic, bleDevice);
    }

    public ChHealthJournalEntry() {
        super(CHARACTERISTIC_UUID);
    }

    @Override
    protected HealthJournalEntryValue processCharacteristicValue() {
        HealthJournalEntryValue healthJournalEntryValue = new HealthJournalEntryValue();
        BluetoothGattCharacteristic c = getBaseGattCharacteristic();

        int year   = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        int month  = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2);
        int day    = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
        int hour   = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4);
        int minute = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 5);
        int second = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6);
        healthJournalEntryValue.mTimestamp = new Date(year, month, day, hour, minute, second);

        int nextOffset = 7;
        int format = -1;

        int flags = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, nextOffset++);
        if ((flags & 0x01) != 0) {
            // 0 	Heart rate status  0 - not present, 1 - present
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            healthJournalEntryValue.mHeartRate = c.getIntValue(format, nextOffset);
            nextOffset += 1;
        }
        if ((flags & 0x02) != 0) {
            // 1 Oxygen saturation status:  0 - not present, 1 - present
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            healthJournalEntryValue.mOxygenSaturation = c.getIntValue(format, nextOffset);
            nextOffset += 1;
        }
        if ((flags & 0x04) != 0) {
            // 2 Temperature status  0 - not present, 1 - present
            format = BluetoothGattCharacteristic.FORMAT_SFLOAT;
            healthJournalEntryValue.mTemperature = c.getIntValue(format, nextOffset);
            nextOffset += 2;
        }
        if ((flags & 0x08) != 0) {
            // 3 Step count status  0 - not present, 1 - present
            format = BluetoothGattCharacteristic.FORMAT_UINT32;
            healthJournalEntryValue.mStepCount = c.getIntValue(format, nextOffset);
            nextOffset += 4;
        }
        if ((flags & 0x16) != 0) {
            // 4 Energy expenditure status  0 - not present, 1 - present
        }
        if ((flags & 0x32) != 0) {
            // 5 Acceleration energy status  0 - not present, 1 - present
        }

        return healthJournalEntryValue;
    }


    public class HealthJournalEntryValue {
        Date mTimestamp;
        int mHeartRate;
        int mOxygenSaturation;
        float mTemperature;
        int mStepCount;
        int mEnergyExpended;
        int mAccelerationEnergy;
    }
}