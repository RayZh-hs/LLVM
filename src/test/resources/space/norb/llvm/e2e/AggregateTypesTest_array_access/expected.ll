define i32 @array_access(ptr %arg0, i32 %arg1) {
entry:
  %element_ptr = gep [10 x i32], ptr %arg0, 0, %arg1
  %element = load i32, ptr %element_ptr
  ret i32 %element
}