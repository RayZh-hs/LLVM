define i32 @sext(i8 %arg0) {
entry:
  %result = sext i8 %arg0 to i32
  ret i32 %result
}