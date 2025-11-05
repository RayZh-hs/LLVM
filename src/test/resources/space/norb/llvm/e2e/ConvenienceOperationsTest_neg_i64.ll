define i64 @neg_operation_i64(i64 %arg0) {
entry:
  %result = sub i64 0, %arg0
  ret i64 %result
}