function viewStatistic(id) {
    const subject = +document.getElementById('subject-filter').value;
    const exam = +document.getElementById('exam-filter').value;
    if (subject === '*' || exam === '*') {
    	return ;
    }
    // send request to URL: /statistic with the body: {teacher, subject, exam}
    const teacher = +id;
    const data = { teacher, subject, exam };
	fetch('/statistic', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(data),
	})
	.then(response => response.json())
	.then(result => {
		const statistic = document.getElementById('statistic');
		if (result.length == 0) {
			statistic.innerHTML = `<p>Not any results found</p>`;
		}
		const mean = jStat.mean(result).toFixed(4);
        const std = jStat.stdev(result).toFixed(4);
		const quantity = result.length;
		document.getElementById('stat-desc').innerHTML = `
			<li>Taken by ${quantity} students</li>
			<li>Mean: ${mean}</li>
			<li>Standard deviation: ${std}</li>
		`;

		const data = result.map(item => ({ y: item }));
		document.getElementById('histogram').innerHTML = '';
		let histogram = new ej.charts.Chart({
			primaryXAxis: {
				majorGridLines: { width: 0 },
				title: 'Score Range',
				minimum: 0, maximum: 10
			},
			chartArea: {
				border: { width: 0 }
			},
			legendSettings: { visible: true },
			primaryYAxis: {
				title: 'Number of Responses',
				minimum: 0,
				majorTickLines: { width: 0 },
				lineStyle: { width: 0 }
			},
			series: [
				{
					type: 'Histogram',
					width: 2,
					yName: 'y',
					name: 'Score',
					fill: 'rgba(20, 75, 170)',
					dataSource: data,
					binInterval: 1,
					marker: {
						dataLabel: {
							visible: true,
							position: 'Top',
							font: {
								fontWeight: '600',
								color: '#ffffff'
							}
						}
					},
					showNormalDistribution: true,
					legendSettings: { visible: true },
					columnWidth: 0.99
				},

			],
			// Set the chart title and tooltip
			title: 'Histogram of the scores of this quiz', tooltip: { enable: true },
			height: '350'
		});
		// Render the chart
		histogram.appendTo('#histogram');

	})
	.catch(error => {
		console.error('Error:', error);
	});
}