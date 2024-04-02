document.addEventListener('DOMContentLoaded', function () {
	function filterTable() {
		var title = document.getElementById('title-search').value.toLowerCase();
		var author = document.getElementById('author-filter').value;
		var exam = document.getElementById('exam-filter').value;
		var sortBy = document.getElementById('sort-filter').value;

		var rows = document.querySelectorAll('.content-table tbody tr');
		rows.forEach((row) => {
			var rowTitle = row.cells[1].textContent.toLowerCase();
			var rowAuthor = row.cells[2].textContent;
			var rowExam = row.cells[5].textContent;
			var rowAuthorId = row.cells[2].getAttribute('author');
			var rowExamId = row.cells[5].getAttribute('exam');

			// Hide the row if it doesn't match the filter criteria
			var titleMatch = rowTitle.includes(title);
			var authorMatch = true;
			if (author != '*') {
				authorMatch = (rowAuthorId === author);
			}
			var examMatch = true;
			if (exam != '*') {
				examMatch = (rowExamId === exam);
			}
			row.style.display = (titleMatch && authorMatch && examMatch) ? '' : 'none';
		});
	}

	document.getElementById('title-search').addEventListener('input', filterTable);
	document.getElementById('author-filter').addEventListener('change', filterTable);
	document.getElementById('exam-filter').addEventListener('change', filterTable);
	document.getElementById('sort-filter').addEventListener('change', () => {
		var sortBy = sortFilter.value;

		switch (sortBy) {
			case 'author-asc':
				sortTable(2, 'asc');
				break;
			case 'author-desc':
				sortTable(2, 'desc');
				break;
			case 'created-asc':
				sortTable(3, 'asc');
				break;
			case 'created-desc':
				sortTable(3, 'desc');
				break;
			case 'modified-asc':
				sortTable(4, 'asc');
				break;
			case 'modified-desc':
				sortTable(4, 'desc');
				break;
			case 'none':
				break;
			default:
				console.error('Invalid sort option');
		}
	});

    function sortTable(columnIndex, order) {
		var rows = Array.from(table.querySelectorAll('.content-table tbody tr'));
		rows.sort(function (a, b) {
			var aValue = a.cells[columnIndex].textContent.trim();
			var bValue = b.cells[columnIndex].textContent.trim();

			if (order === 'asc') {
				return aValue.localeCompare(bValue);
			} else {
				return bValue.localeCompare(aValue);
			}
		});

		table.querySelector('tbody').innerHTML = '';

		rows.forEach(function (row) {
			table.querySelector('tbody').appendChild(row);
		});
	}
});