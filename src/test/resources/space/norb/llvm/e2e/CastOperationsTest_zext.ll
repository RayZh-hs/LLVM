define i32 @zext(i8 %arg0) {
entry:
  %result = zext i8 %arg0 to i32
  ret i32 %result
}