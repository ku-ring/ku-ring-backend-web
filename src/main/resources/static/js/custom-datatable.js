class CustomDatatable extends simpleDatatables.DataTable {
    search(query) {
        if (!this.hasRows) return false

        if(this.hasFilter)
            query = query + " " + this.filterQuery

        query = query.toLowerCase()

        this.currentPage = 1
        this.searching = true
        this.searchData = []

        if (!query.length) {
            this.searching = false
            this.update()
            this.emit("datatable.search", query, this.searchData)
            this.wrapper.classList.remove("search-results")
            return false
        }

        this.clear()

        this.data.forEach((row, idx) => {
            const inArray = this.searchData.includes(row)

            // https://github.com/Mobius1/Vanilla-DataTables/issues/12
            const doesQueryMatch = query.split(" ").reduce((bool, word) => {
                let includes = false
                let cell = null
                let content = null

                for (let x = 0; x < row.cells.length; x++) {

                    cell = row.cells[x]
                    content = cell.hasAttribute("data-content") ? cell.getAttribute("data-content") : cell.textContent

                    if (
                        content.toLowerCase().includes(word) &&
                        this.columns(cell.cellIndex).visible()
                    ) {
                        includes = true
                        break
                    }
                }

                return bool && includes
            }, true)

            if (doesQueryMatch && !inArray) {
                row.searchIndex = idx
                this.searchData.push(idx)
            } else {
                row.searchIndex = null
            }
        })

        this.wrapper.classList.add("search-results")

        if (!this.searchData.length) {
            this.wrapper.classList.remove("search-results")

            this.setMessage(this.options.labels.noRows)
        } else {
            this.update()
        }

        this.emit("datatable.search", query, this.searchData)
    }

    setFilter(filterFlag, filterName, filterQuery) {
        this.hasFilter = filterFlag;
        if(this.hasFilter) {
            this.filterQuery = filterQuery;
        } else {
            this.filterQuery = "";
        }
    }
}