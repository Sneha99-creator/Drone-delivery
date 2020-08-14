#include <stdio.h>
#include <stdlib.h>
#include <limits.h>

#define TMin LONG_MIN
#define TMax LONG_MAX

#include "btest.h"
#include "bits.h"

test_rec test_set[] = {
 {"upperBits", (funct_t) upperBits, (funct_t) test_upperBits, 1, "! ~ & ^ | + << >>", 10, 1,
  {{0, 32},{TMin,TMax},{TMin,TMax}}},
 {"implication", (funct_t) implication, (funct_t) test_implication, 2, "! ~ ^ |", 5, 2,
     {{0,1},{0,1},{TMin,TMax}}},
{"byteSwap", (funct_t) byteSwap, (funct_t) test_byteSwap, 3,
     "! ~ & ^ | + << >>", 25, 2,
    {{TMin, TMax},{0,3},{0,3}}},
 {"sign", (funct_t) sign, (funct_t) test_sign, 1, "! ~ & ^ | + << >>", 10, 3,
     {{-TMax, TMax},{TMin,TMax},{TMin,TMax}}},
 {"sm2tc", (funct_t) sm2tc, (funct_t) test_sm2tc, 1, "! ~ & ^ | + << >>", 15, 4,
  {{TMin, TMax},{TMin,TMax},{TMin,TMax}}},
 {"bitParity", (funct_t) bitParity, (funct_t) test_bitParity, 1, "! ~ & ^ | + << >>", 20, 4,
  {{TMin, TMax},{TMin,TMax},{TMin,TMax}}},
 {"tmax", (funct_t) tmax, (funct_t) test_tmax, 0, "! ~ & ^ | + << >>", 4, 1,
  {{TMin, TMax},{TMin,TMax},{TMin,TMax}}},
 {"rempwr2", (funct_t) rempwr2, (funct_t) test_rempwr2, 2,
    "! ~ & ^ | + << >>", 20, 2,
  {{TMin, TMax},{0,30},{TMin,TMax}}},
 {"isGreater", (funct_t) isGreater, (funct_t) test_isGreater, 2,
    "! ~ & ^ | + << >>", 24, 3,
  {{TMin, TMax},{TMin,TMax},{TMin,TMax}}},
 {"satMul3", (funct_t) satMul3, (funct_t) test_satMul3, 1,
    "~ & ^ | + << >>", 25, 3,
  {{TMin,TMax},{TMin,TMax},{TMin,TMax}}},
 {"trueFiveEighths", (funct_t) trueFiveEighths, (funct_t) test_trueFiveEighths, 1,
    "! ~ & ^ | + << >>", 25, 4,
  {{TMin,TMax},{TMin,TMax},{TMin,TMax}}},
 {"howManyBits", (funct_t) howManyBits, (funct_t) test_howManyBits, 1, "! ~ & ^ | + << >>", 90, 4,
  {{TMin, TMax},{TMin,TMax},{TMin,TMax}}},
  {"", NULL, NULL, 0, "", 0, 0,
   {{0, 0},{0,0},{0,0}}}
};
