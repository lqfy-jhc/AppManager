/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aosp.android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;

import io.github.muntashirakon.AppManager.utils.ParcelUtils;

/**
 * Transfer a large list of Parcelable objects across an IPC.  Splits into
 * multiple transactions if needed.
 *
 * @see BaseParceledListSlice
 */
@SuppressWarnings("rawtypes")
public class ParceledListSlice<T extends Parcelable> extends BaseParceledListSlice<T> {
    public ParceledListSlice(List<T> list) {
        super(list);
    }

    private ParceledListSlice(Parcel in, ClassLoader loader) {
        super(in, loader);
    }

    public static <T extends Parcelable> ParceledListSlice<T> emptyList() {
        return new ParceledListSlice<T>(Collections.emptyList());
    }

    @Override
    public int describeContents() {
        int contents = 0;
        final List<T> list = getList();
        for (T t : list) {
            contents |= t.describeContents();
        }
        return contents;
    }

    @Override
    protected void writeElement(T parcelable, Parcel dest, int callFlags) {
        parcelable.writeToParcel(dest, callFlags);
    }

    @Override
    protected void writeParcelableCreator(T parcelable, Parcel dest) {
        ParcelUtils.writeParcelableCreator(parcelable, dest);
    }

    @Override
    protected Parcelable.Creator<?> readParcelableCreator(Parcel from, ClassLoader loader) {
        return ParcelUtils.readParcelableCreator(from, loader);
    }

    public static final Parcelable.ClassLoaderCreator<ParceledListSlice> CREATOR =
            new Parcelable.ClassLoaderCreator<ParceledListSlice>() {
        @Override
        public ParceledListSlice createFromParcel(Parcel in) {
            return new ParceledListSlice(in, null);
        }

        @Override
        public ParceledListSlice createFromParcel(Parcel in, ClassLoader loader) {
            return new ParceledListSlice(in, loader);
        }

        @Override
        public ParceledListSlice[] newArray(int size) {
            return new ParceledListSlice[size];
        }
    };
}