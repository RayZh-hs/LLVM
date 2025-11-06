define i32 @not_operation(i32 %arg0) {
entry:
  %result = xor i32 %arg0, -1
  ret i32 %result
}