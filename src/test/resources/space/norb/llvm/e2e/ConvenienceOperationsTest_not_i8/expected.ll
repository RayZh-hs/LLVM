define i8 @not_operation_i8(i8 %arg0) {
entry:
  %result = xor i8 %arg0, -1
  ret i8 %result
}