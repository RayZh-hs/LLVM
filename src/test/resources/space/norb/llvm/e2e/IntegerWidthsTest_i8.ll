define i8 @i8_ops(i8 %arg0, i8 %arg1) {
entry:
  %temp1 = add i8 %arg0, %arg1
  %temp2 = and i8 %temp1, 15
  %result = xor i8 %temp2, 255
  ret i8 %result
}