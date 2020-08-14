/* Testing Code */

#include <limits.h>
int test_upperBits(int x) {
  int _32_minus_x = ~x + 33;
  return (!x) ? 0 : ( (~0) << _32_minus_x );
}
int test_implication(int x, int y)
{
  return !(x & (!y));
}
int test_byteSwap(int x, int n, int m)
{
    /* little endiamachine */
    /* least significant byte stored first */

    unsigned int nmask, mmask;

    switch(n) {
    case 0:
      nmask = x & 0xFF;
      x &= 0xFFFFFF00;
      break;
    case 1:
      nmask = (x & 0xFF00) >> 8;
      x &= 0xFFFF00FF;
      break;
    case 2:
      nmask = (x & 0xFF0000) >> 16;
      x &= 0xFF00FFFF;
      break;
    default:
      nmask = ((unsigned int)(x & 0xFF000000)) >> 24;
      x &= 0x00FFFFFF;
      break;
    }

    switch(m) {
    case 0:
      mmask = x & 0xFF;
      x &= 0xFFFFFF00;
      break;
    case 1:
      mmask = (x & 0xFF00) >> 8;
      x &= 0xFFFF00FF;
      break;
    case 2:
      mmask = (x & 0xFF0000) >> 16;
      x &= 0xFF00FFFF;
      break;
    default:
      mmask = ((unsigned int)(x & 0xFF000000)) >> 24;
      x &= 0x00FFFFFF;
      break;
    }

    nmask <<= 8*m;
    mmask <<= 8*n;

    return x | nmask | mmask;
}
int test_sign(int x) {
    if ( !x ) return 0;
    return (x < 0) ? -1 : 1;
}
int test_sm2tc(int x) {
  int sign = x < 0;
  int mag = x & 0x7FFFFFFF;
  return sign ? -mag : mag;
}
int test_bitParity(int x) {
  int result = 0;
  int i;
  for (i = 0; i < 32; i++)
    result ^= (x >> i) & 0x1;
  return result;
}
int test_tmax(void) {
  return 0x7FFFFFFF;
}
int test_rempwr2(int x, int n)
{
    int p2n = 1<<n;
    return x%p2n;
}
int test_isGreater(int x, int y)
{
  return x > y;
}
int test_satMul3(int x)
{
  if ((x+x+x)/3 != x)
    return x < 0 ? 0x80000000 : 0x7FFFFFFF;
  else
    return 3*x;
}
int test_trueFiveEighths(int x)
{
  return (int) (((long long int) x * 5)/8);
}
int test_howManyBits(int x) {
    unsigned int a, cnt;

    x = x<0 ? -x-1 : x;
    a = (unsigned int)x;
    for (cnt=0; a; a>>=1, cnt++)
        ;

    return (int)(cnt + 1);
}
