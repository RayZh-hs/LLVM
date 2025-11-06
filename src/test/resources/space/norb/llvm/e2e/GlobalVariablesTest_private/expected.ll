@private_global = private global i32 100

define i32 @get_private_global() {
entry:
  %loaded_value = load i32, ptr @private_global
  ret i32 %loaded_value
}