A sliding menu for Android, very much like the Google+ and Facebook apps have.

Based upon the great work done by stackoverflow user Scirocco (http://stackoverflow.com/a/11367825/361413),
thanks a lot!

The XML parsing code comes from https://github.com/darvds/RibbonMenu, thanks!

![SlideMenu](https://github.com/bk138/LibSlideMenu/raw/master/screenshot1.png)


Usage
=====

Menus are created in xml as normal, adding text and possibly an icon.

In the activity where you want a SlideMenu, just instantiate one and call its
show() method where appropriate, i.e. on app-icon click in action bar or on button
click. The SlideMenu constructor will require you to have the calling class implement
the SlideMenu callback to get notified about menu item clicks.

The sample activity shows how it all works.


Credit
======

Thanks go out to stackoverflow user scirocco (http://stackoverflow.com/users/1150188/scirocco)
whose work we just slightly extended and also to David Scott for the XML parsing code in RibbonMenu
(https://github.com/darvds/RibbonMenu) that we used to complete this implementation.


License
=======

Copyright 2012 CoboltForge, David Scott and http://stackoverflow.com/users/1150188/scirocco

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.