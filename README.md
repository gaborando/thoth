# THOTH - Template and Printing Service

## Pipes
### Padding
```
{{ value | padding(size,'character') }}
```
```java
String.format("%" + size + "s", value).replace(' ', character)
```
### Trim
```
{{ value | trim }}
```
```java
value.trim()
```
### Date
```
{{ value | date('pattern') }}
```
```java
ZonedDateTime.parse(value).format(DateTimeFormatter.ofPattern(pattern));
```
### Number
```
{{ value | number('pattern') }}
```
```java
String.format("%" + format + "f", Double.parseDouble(value));
```