package org.qupla.utils;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TritUtilsTest {

    @Test
    public void detectTritTest() {
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRIT_FMT}, TritUtils.detectFormat("1-0"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRIT_FMT}, TritUtils.detectFormat("01"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRIT_FMT, TritUtils.DATA_FORMAT.DECIMAL}, TritUtils.detectFormat("1"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRIT_FMT, TritUtils.DATA_FORMAT.DECIMAL}, TritUtils.detectFormat("10"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRIT_FMT}, TritUtils.detectFormat("10-"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRYTE}, TritUtils.detectFormat("ABC9"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRYTE, TritUtils.DATA_FORMAT.DECIMAL}, TritUtils.detectFormat("9"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRYTE, TritUtils.DATA_FORMAT.DECIMAL}, TritUtils.detectFormat("9999"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.TRYTE}, TritUtils.detectFormat("9A"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.DECIMAL}, TritUtils.detectFormat("98"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.DECIMAL}, TritUtils.detectFormat("12"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.DECIMAL}, TritUtils.detectFormat("03"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.INVALID}, TritUtils.detectFormat("a"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.INVALID}, TritUtils.detectFormat("A-"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.INVALID}, TritUtils.detectFormat("9-"));
        Assert.assertArrayEquals(new TritUtils.DATA_FORMAT[]{TritUtils.DATA_FORMAT.INVALID}, TritUtils.detectFormat("2-"));
    }

    @Test
    public void trit2decimalTest() {
        Assert.assertEquals(BigInteger.valueOf(1), TritUtils.trit2Decimal(new TRIT[]{TRIT.O}));
        Assert.assertEquals(BigInteger.valueOf(-1), TritUtils.trit2Decimal(new TRIT[]{TRIT.M}));
        Assert.assertEquals(BigInteger.valueOf(0), TritUtils.trit2Decimal(new TRIT[]{TRIT.Z}));
        Assert.assertEquals(BigInteger.valueOf(4), TritUtils.trit2Decimal(new TRIT[]{TRIT.O, TRIT.O}));
        Assert.assertEquals(BigInteger.valueOf(-2), TritUtils.trit2Decimal(new TRIT[]{TRIT.O, TRIT.M}));
    }

    @Test
    public void decimal2tritTest() {
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z}, TritUtils.long2Trits(0));
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z}, TritUtils.bigInt2Trits(BigInteger.ZERO));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.M}, TritUtils.long2Trits(-2));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.M}, TritUtils.bigInt2Trits(BigInteger.valueOf(-2)));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.O}, TritUtils.long2Trits(4));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.O}, TritUtils.bigInt2Trits(BigInteger.valueOf(4)));
    }

    @Test
    public void tryte2tritTest() {
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z, TRIT.Z, TRIT.Z}, TritUtils.trytes2Trits("9"));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.Z, TRIT.Z}, TritUtils.trytes2Trits("A"));
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z, TRIT.Z, TRIT.Z, TRIT.O, TRIT.Z, TRIT.Z}, TritUtils.trytes2Trits("9A"));
    }

    @Test
    public void trit2TryteTest() {
        Assert.assertEquals("9", TritUtils.trit2Trytes(new TRIT[]{TRIT.Z}));
        Assert.assertEquals("9", TritUtils.trit2Trytes(new TRIT[]{TRIT.Z, TRIT.Z}));
        Assert.assertEquals("9", TritUtils.trit2Trytes(new TRIT[]{TRIT.Z, TRIT.Z, TRIT.Z}));
        Assert.assertEquals("A", TritUtils.trit2Trytes(new TRIT[]{TRIT.O}));
        Assert.assertEquals("A", TritUtils.trit2Trytes(new TRIT[]{TRIT.O, TRIT.Z}));
        Assert.assertEquals("A", TritUtils.trit2Trytes(new TRIT[]{TRIT.O, TRIT.Z, TRIT.Z}));
        Assert.assertEquals("AA", TritUtils.trit2Trytes(new TRIT[]{TRIT.O, TRIT.Z, TRIT.Z, TRIT.O}));
    }

    @Test
    public void trit2TritesToTryteTest() {
        Assert.assertEquals("A", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("A"))));
        Assert.assertEquals("B", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("B"))));
        Assert.assertEquals("C", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("C"))));
        Assert.assertEquals("D", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("D"))));
        Assert.assertEquals("E", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("E"))));
        Assert.assertEquals("F", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("F"))));
        Assert.assertEquals("G", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("G"))));
        Assert.assertEquals("H", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("H"))));
        Assert.assertEquals("I", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("I"))));
        Assert.assertEquals("J", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("J"))));
        Assert.assertEquals("K", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("K"))));
        Assert.assertEquals("L", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("L"))));
        Assert.assertEquals("M", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("M"))));
        Assert.assertEquals("N", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("N"))));
        Assert.assertEquals("O", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("O"))));
        Assert.assertEquals("P", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("P"))));
        Assert.assertEquals("Q", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("Q"))));
        Assert.assertEquals("R", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("R"))));
        Assert.assertEquals("S", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("S"))));
        Assert.assertEquals("T", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("T"))));
        Assert.assertEquals("U", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("U"))));
        Assert.assertEquals("V", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("V"))));
        Assert.assertEquals("W", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("W"))));
        Assert.assertEquals("X", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("X"))));
        Assert.assertEquals("Y", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("Y"))));
        Assert.assertEquals("Z", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("Z"))));
        Assert.assertEquals("9", (TritUtils.trit2Trytes(TritUtils.trytes2Trits("9"))));
    }

    @Test
    public void trit2StringTest() {
        Assert.assertEquals("0", TritUtils.trit2String(new TRIT[]{TRIT.Z}));
        Assert.assertEquals("00", TritUtils.trit2String(new TRIT[]{TRIT.Z, TRIT.Z}));
        Assert.assertEquals("000", TritUtils.trit2String(new TRIT[]{TRIT.Z, TRIT.Z, TRIT.Z}));
        Assert.assertEquals("1", TritUtils.trit2String(new TRIT[]{TRIT.O}));
        Assert.assertEquals("10", TritUtils.trit2String(new TRIT[]{TRIT.O, TRIT.Z}));
        Assert.assertEquals("100", TritUtils.trit2String(new TRIT[]{TRIT.O, TRIT.Z, TRIT.Z}));
        Assert.assertEquals("1001", TritUtils.trit2String(new TRIT[]{TRIT.O, TRIT.Z, TRIT.Z, TRIT.O}));
        Assert.assertEquals("100-", TritUtils.trit2String(new TRIT[]{TRIT.O, TRIT.Z, TRIT.Z, TRIT.M}));
    }

    @Test
    public void testDecimalToTrits() {
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z, TRIT.O, TRIT.M, TRIT.O, TRIT.O, TRIT.O}, TritUtils.long2Trits(345));
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z, TRIT.O, TRIT.M, TRIT.O, TRIT.O, TRIT.O}, TritUtils.bigInt2Trits(BigInteger.valueOf(345)));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.M, TRIT.O, TRIT.M, TRIT.M, TRIT.O}, TritUtils.long2Trits(142));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.M, TRIT.O, TRIT.M, TRIT.M, TRIT.O}, TritUtils.bigInt2Trits(BigInteger.valueOf(142)));
    }

    private static final String tritsEric ="-110-10101-10000000-0-11-010---00---1-01-010--101-0--1-01-0--0111---1111-01-1----0111-11-1001001111---11111-1010101010-11-10100---01-011-001--1--0110010-01----00001---1101101100--111-00010-110-0-011001-010-0-1010011-00-00--0-1-11----0--0-10--0--010-0-11-111000-111-100-1--01-1-1-0101---0000101-011-10-10-110-0-001-1-0---1--1-01110010010-1-0----0-101-10---1-1-01--01010-110------11-0--111-0---0----101-11-01-111001-0101001-10--0-0111---0110101-11-101-10--1-1-1-010---011-0-010-0--0101-10101110-1---11001-1-001-0---00-10110000--001-0-1--101-00-0100--01-010-00-11--00000----0110110-1-0010100-11---100110010-1-00011-0-010-110---1010----1-1-10-0---1--10-0-0--10--1-1-10110-1-011--010101-0--0-0011110101101-101-01-111-1100--01----01100";
    private static final String decimalEric = "31394377602787912851322825916993645509620879394572349004620407904030879201638093222107259852444675868774947467107602287494329219725945847633998526837710581855047049793511059960367687930317261699496974054026016791523256785248487291150263992247227148870325986497182694871919431134890179617789139068941610519590227393281331517878419931335334261432103";

    @Test
    public void testTritsToDecimal() {
        TRIT[] trits = TritUtils.stringToTrits(tritsEric);
        BigInteger expectedDecimal = new BigInteger(decimalEric, 10);
        Assert.assertEquals(expectedDecimal, TritUtils.trit2Decimal(trits));
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z, TRIT.O, TRIT.M, TRIT.O, TRIT.O, TRIT.O}, TritUtils.long2Trits(345));
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z, TRIT.O, TRIT.M, TRIT.O, TRIT.O, TRIT.O}, TritUtils.bigInt2Trits(BigInteger.valueOf(345)));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.M, TRIT.O, TRIT.M, TRIT.M, TRIT.O}, TritUtils.long2Trits(142));
        Assert.assertArrayEquals(new TRIT[]{TRIT.O, TRIT.M, TRIT.O, TRIT.M, TRIT.M, TRIT.O}, TritUtils.bigInt2Trits(BigInteger.valueOf(142)));
    }

    @Test
    public void limitTest(){
        System.out.println(TritUtils.trit2Decimal(TritUtils.trytes2Trits("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM")));
    }

    @Test
    public void floatToTritsTest(){
        int manSize = 9;
        int expSize = 3;
        printFloat("1",TritUtils.floatToTrits(new BigDecimal("1"),manSize,expSize),manSize);
        printFloat("1.0",TritUtils.floatToTrits(new BigDecimal("1.0"),manSize,expSize),manSize);
        printFloat("2",TritUtils.floatToTrits(new BigDecimal("2"),manSize,expSize),manSize);
        printFloat("4",TritUtils.floatToTrits(new BigDecimal("4"),manSize,expSize),manSize);
        printFloat("11",TritUtils.floatToTrits(new BigDecimal("11"),manSize,expSize),manSize);
        printFloat("1.1",TritUtils.floatToTrits(new BigDecimal("1.1"),manSize,expSize),manSize);
        printFloat("-1.1",TritUtils.floatToTrits(new BigDecimal("-1.1"),manSize,expSize),manSize);
        printFloat("0.0",TritUtils.floatToTrits(new BigDecimal("0.0"),manSize,expSize),manSize);
    }

    @Test
    public void normalizeTest(){
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z,TRIT.Z,TRIT.O},TritUtils.normalize(new TRIT[]{TRIT.Z,TRIT.Z,TRIT.O},3));
        Assert.assertArrayEquals(new TRIT[]{TRIT.Z,TRIT.Z,TRIT.O},TritUtils.normalize(new TRIT[]{TRIT.O},3));
    }

    private void printFloat(String val, TRIT[] trits, int manSize){
        TRIT[] m = new TRIT[manSize];
        TRIT[] e = new TRIT[trits.length-manSize];
        System.arraycopy(trits,0,m,0,manSize);
        System.arraycopy(trits,manSize,e,0,trits.length-manSize);
        BigInteger mantissa = TritUtils.trit2Decimal(m);
        BigInteger exponent = TritUtils.trit2Decimal(e);
        BigDecimal f = new BigDecimal(Math.pow(3d,exponent.doubleValue()));
        BigDecimal fm = new BigDecimal(mantissa);
        BigDecimal result = fm.multiply(f);
        System.out.println(val+" = "+mantissa+"* 3^"+exponent+ " = "+result.toString());
    }

}
