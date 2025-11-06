@constant_global = external constant i32 12345

define i32 @get_constant_global() {
entry:
  %loaded_value = load i32, ptr @constant_global
  ret i32 %loaded_value
}