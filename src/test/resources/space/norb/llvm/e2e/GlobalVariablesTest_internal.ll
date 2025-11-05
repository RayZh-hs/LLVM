@internal_global = internal global i32 200

define i32 @get_internal_global() {
entry:
  %loaded_value = load i32, ptr @internal_global
  ret i32 %loaded_value
}