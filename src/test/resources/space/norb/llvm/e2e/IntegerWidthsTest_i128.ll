define i128 @i128_ops(i128 %arg0, i128 %arg1) {
entry:
  %temp1 = mul i128 %arg0, %arg1
  %temp2 = add i128 %temp1, 1
  %temp3 = or i128 %temp2, -1
  %result = and i128 %temp3, -1
  ret i128 %result
}