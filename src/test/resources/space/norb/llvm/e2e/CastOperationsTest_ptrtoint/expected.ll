define i64 @ptr_to_int(ptr %arg0) {
entry:
  %converted = ptrtoint ptr %arg0 to i64
  ret i64 %converted
}
