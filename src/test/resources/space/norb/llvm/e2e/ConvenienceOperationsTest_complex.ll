define i32 @complex_convenience_ops(i32 %arg0, i32 %arg1) {
entry:
  %not_arg0 = xor i32 %arg0, -1
  %neg_arg1 = sub i32 0, %arg1
  %temp = and i32 %not_arg0, %neg_arg1
  %result = xor i32 %temp, -1
  ret i32 %result
}