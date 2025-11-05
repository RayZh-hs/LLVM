define i16 @i16_ops(i16 %arg0, i16 %arg1) {
entry:
  %temp1 = mul i16 %arg0, %arg1
  %temp2 = or i16 %temp1, 4095
  %result = sub i16 %temp2, 1000
  ret i16 %result
}