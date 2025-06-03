package gridley

import react.FC
import react.Props
import util.Entities
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

external interface GridleyPagerProps : Props {
    var totalPages: Int
    var currentPage: Int
    var onPageSelect: (Int) -> Unit
}

private val PageLink = ClassName("page-link clickable")
private val PageLinkDisabled = ClassName("page-link disabled")
private val PageItem = ClassName("page-item")

/** Page navigation. */
val GridleyPager =
    FC<GridleyPagerProps> { props ->
        val preceding = props.currentPage
        val following = props.totalPages - props.currentPage - 1

        h.div {
            className = ClassName("grid-pager-box")
            h.div {
                className = ClassName("grid-pager-elem")
                h.nav {
                    h.ul {
                        className = ClassName("pagination")
                        h.li {
                            className = PageItem
                            h.span {
                                className = if (1 < preceding) PageLink else PageLinkDisabled
                                +"⟪"
                                onClick = { props.onPageSelect(0) }
                            }
                        }
                        h.li {
                            className = PageItem
                            h.span {
                                className = if (0 < preceding) PageLink else PageLinkDisabled
                                +"⟨"
                                onClick = { props.onPageSelect(preceding - 1) }
                            }
                        }
                        h.li {
                            className = PageItem
                            h.span {
                                className = PageLinkDisabled
                                +(1 + preceding).toString()
                            }
                        }
                        h.li {
                            className = PageItem
                            h.span {
                                className = if (0 < following) PageLink else PageLinkDisabled
                                +"⟩"
                                onClick = { props.onPageSelect(preceding + 1) }
                            }
                        }
                        h.li {
                            className = PageItem
                            h.span {
                                className = if (1 < following) PageLink else PageLinkDisabled
                                +"》"
                                onClick = { props.onPageSelect(props.totalPages - 1) }
                            }
                        }
                    }
                }
            }
            h.div {
                className = ClassName("grid-pager-elem")
                h.span {
                    +Entities.NBSP
                    h.small {
                        +"of ${props.totalPages} page(s)."
                    }
                }
            }
        }
    }
