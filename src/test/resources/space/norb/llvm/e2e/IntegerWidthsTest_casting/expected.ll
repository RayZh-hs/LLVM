define i128 @width_casting_ops(i64 %arg0) {
entry:
  %temp1 = zext i64 %arg0 to i128
  %temp2 = mul i128 %temp1, 2
  %temp3 = trunc i128 %temp2 to i64
  %temp4 = sext i64 %temp3 to i128
  %result = add i128 %temp4, 1000000
  ret i128 %result
}