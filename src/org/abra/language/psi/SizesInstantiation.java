package org.abra.language.psi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//TODO : check real usage of this

public class SizesInstantiation {

    private List<Integer> sizes = new ArrayList<>();

    public boolean match(Integer... integers){
        if(integers.length!=sizes.size())return false;
        int i=0;
        for(Integer integer:integers){
            if(integer.intValue()!=sizes.get(i))return false;
            i++;
        }
        return true;
    }

    public void add(int resolvedSize) {
        sizes.add(resolvedSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizesInstantiation that = (SizesInstantiation) o;
        if(that.sizes.size()!=sizes.size())return false;
        for(int i=0;i<sizes.size();i++){
            if(sizes.get(i).intValue()!=that.sizes.get(i).intValue())return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for(int i=0;i<sizes.size();i++){
            h=h+(i*Objects.hash(sizes.get(i)));
        }
        return h;
    }
}
