define i32 @alloca_store_load(i32 %arg0) {
entry:
  %var = alloca i32
  store i32 %arg0, ptr %var
  %loaded = load i32, ptr %var
  ret i32 %loaded
}