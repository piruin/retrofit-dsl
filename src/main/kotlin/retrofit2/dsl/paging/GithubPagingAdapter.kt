/**
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 */

package retrofit2.dsl.paging

import retrofit2.Response

/**
 * Page link class to be used to determine the links to other pages of request
 * responses encoded in the current response. These will be present if the
 * result set size exceeds the per page limit.
 *
 * [Original PageLinks](https://github.com/eclipse/egit-github/blob/master/org.eclipse.egit.github.core/src/org/eclipse/egit/github/core/client/PageLinks.java)
 */
class GithubPagingAdapter : PagingAdapter {

    override fun <T> parse(response: Response<T>): Page? {
        var _next: Int? = null
        var _last: Int? = null
        var _perPage: Int? = null

        val linkHeader = response.headers()[HEADER_LINK]
        if (linkHeader != null) {
            val links = linkHeader.split(DELIM_LINKS)
            for (link in links) {
                val segments = link.split(DELIM_LINK_PARAM)
                if (segments.size < 2)
                    continue

                var linkPart = segments[0].trim()
                if (!linkPart.startsWith("<") || !linkPart.endsWith(">"))
                    continue
                linkPart = linkPart.substring(1, linkPart.length - 1)

                for (i in 1 until segments.size) {
                    val rel = segments[i].trim().split("=")
                        .toTypedArray()
                    if (rel.size < 2 || !META_REL.equals(rel[0]))
                        continue
                    var relValue = rel[1]
                    if (relValue.startsWith("\"") && relValue.endsWith("\""))
                        relValue = relValue.substring(1, relValue.length - 1)

                    if (META_LAST.equals(relValue))
                        _last = linkPart.getPageParam()!!
                    else if (META_NEXT.equals(relValue))
                        _next = linkPart.getPageParam()!!
                    _perPage = linkPart.getPerPageParam() ?: _perPage
                }
            }
        } else {
            response.headers()[HEADER_NEXT]?.let {
                _next = it.getPageParam()
                _perPage = it.getPerPageParam()
            }

            response.headers()[HEADER_LAST]?.let {
                _last = it.getPageParam()
                _perPage = it.getPerPageParam()
            }
        }
        return if (_next != null && _last != null && _perPage != null) {
            Page(_next!!, _last!!, _perPage!!)
        } else null
    }

    fun String.getPageParam() = REGEX_PAGE_PARAM.find(this)?.groupValues?.get(1)?.toInt()
    fun String.getPerPageParam() = REGEX_PER_PAGE_PARAM.find(this)?.groupValues?.get(1)?.toInt()

    companion object {

        val DELIM_LINKS = Regex(",")

        val DELIM_LINK_PARAM = Regex(";")

        const val HEADER_LINK = "Link"
        const val META_REL = "rel"
        const val META_LAST = "last"
        const val META_NEXT = "next"
        const val HEADER_NEXT = "X-Next"
        const val HEADER_LAST = "X-Last"

        val REGEX_PAGE_PARAM = Regex("""[?&]page=(\d*?)(&|${'$'})""")

        val REGEX_PER_PAGE_PARAM = Regex("""[?&]per_page=(\d*?)(&|$)""")
    }
}
