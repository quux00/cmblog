(ns thornydev.cmblog.w3.util
  (:import (org.apache.commons.lang3 StringEscapeUtils)))

(defn escape
  "Does an HTML escape operation on the string
  passed in.  Returns escaped string or empty
  string if nil is passed in."
  [s]
  (if (seq s)
    (StringEscapeUtils/escapeHtml4 s)
    ""))

