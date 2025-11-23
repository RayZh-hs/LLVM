define i32 @srem(i32 %arg0, i32 %arg1) {
entry:
  %result = srem i32 %arg0, %arg1
  ret i32 %result
}