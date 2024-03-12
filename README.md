# Java widget for visualization ECG

The project implements a widget for dynamic visualization of constantly incoming ECG data

## Introduction

The project is a ported _java_- version of the application https://github.com/mk590901/stateful_custom_widget_painter.

## Changes and additions

The ported version retains all the properties of the original _flutter_- version and contains some additions. 
The main difference in the _java_- implementation: the widget is located inside the wrapper _CardView_. The latter has a built-in switch for rendering mode: _overlay_ ⇆ _flow_ and vice versa, and a delete button. Adding widgets is done dynamically by pressing the action button.

## Movie

https://github.com/mk590901/java_graph_widget_viewer/assets/125393245/39c46f88-919e-4c36-9b59-b134060e376c

