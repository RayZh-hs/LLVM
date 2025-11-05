define i64 @i64_ops(i64 %arg0, i64 %arg1) {
entry:
  %temp1 = add i64 %arg0, %arg1
  %temp2 = sdiv i64 %temp1, 2
  %temp3 = and i64 %temp2, 281474976710655
  %result = xor i64 %temp3, 9223372036854775808
  ret i64 %result
}