define i32 @neg_operation(i32 %arg0) {
entry:
  %result = sub i32 0, %arg0
  ret i32 %result
}