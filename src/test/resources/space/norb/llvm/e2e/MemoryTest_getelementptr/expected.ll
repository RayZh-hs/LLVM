define i32 @getelementptr_example(ptr %arg0) {
entry:
  %ptr_to_elem = getelementptr i32, ptr %arg0, i32 2
  %loaded = load i32, ptr %ptr_to_elem
  ret i32 %loaded
}