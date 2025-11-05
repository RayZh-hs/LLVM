define i32 @complex_binary_ops(i32 %arg0, i32 %arg1, i32 %arg2) {
entry:
  %temp1 = mul i32 %arg0, %arg1
  %temp2 = add i32 %temp1, %arg2
  %temp3 = and i32 %temp2, 255
  %result = xor i32 %temp3, 128
  ret i32 %result
}