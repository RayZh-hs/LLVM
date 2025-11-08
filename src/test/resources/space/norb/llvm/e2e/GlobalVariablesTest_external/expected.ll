@external_global = global i32 42

define i32 @get_external_global() {
entry:
  %loaded_value = load i32, ptr @external_global
  ret i32 %loaded_value
}