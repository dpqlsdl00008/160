/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

import server.Randomizer;
import tools.data.OutPacket;

public class PlayerRandomStream {

    private transient long seed1, seed2, seed3;

    public PlayerRandomStream() {
        final int v2 = 214013 * (214013 * (214013 * Randomizer.nextInt()+ 2531011) + 2531011) + 2531011;
        this.CRand32__Seed(v2, v2, v2);
    }

    public final void CRand32__Seed(final long s1, final long s2, final long s3) {
        seed1 = s1 | 0x100000;
        seed2 = s2 | 0x1000;
        seed3 = s3 | 0x10;
    }

    public final long CRand32__Random() { // 004093E0
        /*
        long v8 = ((this.seed1 & 0xFFFFFFFE) << 12) ^ ((this.seed1 & 0x7FFC0 ^ (this.seed1 >> 13)) >> 6);
        long v9 = 16 * (this.seed2 & 0xFFFFFFF8) ^ (((this.seed2 >> 2) ^ this.seed2 & 0x3F800000) >> 23);
        long v10 = ((this.seed3 & 0xFFFFFFF0) << 17) ^ (((this.seed3 >> 3) ^ this.seed3 & 0x1FFFFF00) >> 8);
        return (v8 ^ v9 ^ v10) & 0xffffffffL; // to be confirmed, I am not experienced in converting signed > unsigned
                */
        long v1 = this.seed1;
        long v2 = this.seed2;
        long v3 = this.seed3;
        long v4 = this.seed1;
        long v5 = this.seed1 >> 6;
        long v7 = (v1 << 12) ^ (v1 >> 19) ^ ((v5 ^ (v4 << 12))) & 0x1FFF;
        long v8 = 16 * v2 ^ (v2 >> 25) ^ ((16 * v2) ^ (v2 >> 23)) & 0x7F;
        long v9 = (v3 >> 8) ^ (v3 << 17);
        long v10 = (v3 >> 11) ^ (v3 << 17) ^ v9 & 0x1FFFFF;
        long result = v7 ^ v8 ^ v10;
        return result;
        //long v8 = ((this.seed1 & 0xFFFFFFFE) << 12) ^ ((this.seed1 & 0x7FFC0 ^ (this.seed1 >> 13)) >> 6);
        //long v9 = 16 * (this.seed2 & 0xFFFFFFF8) ^ (((this.seed2 >> 2) ^ this.seed2 & 0x3F800000) >> 23);
        //long v10 = ((this.seed3 & 0xFFFFFFF0) << 17) ^ (((this.seed3 >> 3) ^ this.seed3 & 0x1FFFFF00) >> 8);
        //return (v8 ^ v9 ^ v10) & 0xffffffffL; // to be confirmed, I am not experienced in converting signed > unsigned        
    }

    public final void connectData(final OutPacket mplew) {
        long v5 = CRand32__Random();
        long s2 = CRand32__Random();
        long v6 = CRand32__Random();

        CRand32__Seed(v5, s2, v6);

        mplew.writeInt((int) v5);
        mplew.writeInt((int) s2);
        mplew.writeInt((int) v6);
    }
}
