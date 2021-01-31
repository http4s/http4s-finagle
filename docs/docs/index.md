---
layout: main
title: "Http4s Finagle"
---

Table of Contents
=================
{% assign titles = sidebar.titles %}

<ul>
  {% for item in titles %}
      <li>
        {% if item.url %}
            <a href="{{ site.baseurl }}/{{ item.url }}">{{ item.title }}</a>
        {% else %}
            {{ item.title }}
        {% endif %}
        {% if item.subsection %}
            {% assign titles = item.subsection %}
            {% include "table-of-contents" %}
        {% endif %}
      </li>
  {% endfor %}
</ul>
