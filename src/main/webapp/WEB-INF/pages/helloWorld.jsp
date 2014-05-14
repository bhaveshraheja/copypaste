<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
	<!-- Load Jquery --> 
	<script src="<c:url value="/resources/js/jquery-1.11.10.min.js" />" >
	</script>
	<!-- Load the Autocomplete Widget -->
	<script src="<c:url value="/resources/js/jquery.autocomplete.js" />" >
	</script>
	<!-- Load the JQWidgets Core -->
	<script src="<c:url value="/resources/js/jqwidgets/jqxcore.js" />" >
	</script>
	<!-- Load the JQWidgets Plugin  for Buttons -->
	<script src="<c:url value="/resources/js/jqwidgets/jqxbuttons.js" />" >
	</script>
	<!-- Load the JQWidgets Plugin  for the Listbox -->
	<script src="<c:url value="/resources/js/jqwidgets/jqxlistbox.js" />" >
	</script>
	<!-- Load the JQWidgets Plugin   for the Scrollbar-->
	<script src="<c:url value="/resources/js/jqwidgets/jqxscrollbar.js" />" >
	</script>
	
	
	
	<link href="<c:url value="/resources/css/style.css" />" rel="stylesheet">
	<link href="<c:url value="/resources/js/jqwidgets/styles/jqx.base.css" />" rel="stylesheet">
	
</head>
<body>
	<section class="container">
		
		<header>
			<h1>Copy Paste for the Web</h1>
		</header> <!-- End of heading -->
		
		<article class="main">
		<table>
				<caption>
				</caption>
			<colgroup span="2" title="title" />
			<tbody>
			<c:forEach var="i" begin="0" end="${ncols}">
				<tr>
					<c:forEach var="j" begin="0" end="${nrows}">
						<td> 
							<input type="text" class="cell" id="cell_<c:out value="${i}"/>_<c:out value="${j}"/>"/>
							<button onclick="getRecommendations(<c:out value="${i}"/>,<c:out value="${j}"/>)">
							run
							</button>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
			
			</tbody>
		</table>
		<section class="status" style="float:left; width:60%">
			<header>
				<h1>Enter source information</h1>
			</header>
			<section>
				<p>Web source: <input type="url" class="source" placeholder="Enter URL" size="30"/></p>
				<button class="set_source">Set Source</button>
			</section>
			
			<header>
				<h1> Provenance/Feedback & Recommendation Information </h1>
			</header>
			<section>
				 <div id="eventlog" style="height:200px; overflow:scroll"></div>
			</section>
			
			<header>
				<h1>Learning Rate Information</h1>
			</header>
			<section>
				Learning Rate: <input type="number" class="learning_rate" placeholder="1.0"/>
				Delta: <input type="number" class="delta" placeholder="1.0"/>
				<button class="set_rates">Set Rates</button> 
			</section>
			
			
			
		</section>
		<aside>
			<div id='jqxlistbox'>
	        </div>
	        <input id="button" type="button" value="Select Item" />
	        
			<header>
				<h1>Status Information</h1>
			</header>
			<section>
				<p> GATE Status: ${gateStatus}</p>
				<p> <div id="source_status"></div></p> 
			</section>
		</aside>
	
	</article> <!--  End of main -->
	
	
	
	</section> <!--  End of Container -->
	
	<script>
		$(document).ready(function() {
			
			
			$('.cell').change(function(){
				var cellid = this.id;
				var id_parts = cellid.split('_');
				var column = id_parts[2];
				var row = id_parts[1];
				var text = $(this).val();
				//alert("Text is "+text);
				
				getRecommendations(row, column);
			});
			
			$('.set_source').click (function () {
					$.ajax ( {
						url:'${pageContext.request.contextPath}/setSource',
						data:'url='+ $('.source').val(),
						dataType: 'text' //Expected data type as String
					}).done(function(response) {
						$('#source_status').html(response);
					});
			});
			
			
			$('.set_rates').click(function () {
				var eta = parseFloat($('.learning_rate').val());
				var delta = parseFloat($('.delta').val());
				$.ajax ( {
					url:'${pageContext.request.contextPath}/updateRates',
					data: {
						'eta':eta,
						'delta':delta
					}
				
				}).done(function(response) {
					$('#eventlog').html(response+" <--Learning rates updated, eta:"+eta+"|delta:"+delta);
				});
			})
			
		});
	</script>
	<script>
		function getRecommendations(row, column) {
			$("#button").unbind('click');
			
			console.log("R="+row+"|C="+column);
			console.log("#cell_"+row+"_"+column);
			//alert("#cell_"+row+"_"+column);
			var text = $("#cell_"+row+"_"+column).val();
			//alert("Text is "+text);
			if(column == 0) {
				
				$.ajax({
					url:'${pageContext.request.contextPath}/getSugs',
					data: {
						'text':text,	
						'column':column
					},
					dataType: "text"
					}).done(function(response) {
					console.log(response);
					var source = []
					var feature = []
					var prov = []
					var current, name, bit, curprov;
					var responseObjects = JSON.parse(response);
					//alert(responseObjects[0])
					
					for(var index = 0; index < responseObjects.length; index ++) {
						current = responseObjects[index];
						name = current['suggestionText'];
						bit = current['feature'];
						curprov = current['provInfo'];
						prov.push(curprov);
						feature.push(bit);
						source.push(name);
					}
					
					var selectedText;
					$("#jqxlistbox").jqxListBox({ source: source, width: '200px', height: '200px' });
					
					$('#jqxlistbox').bind('select', function (event) {
	                    var args = event.args;
	                    var item = $('#jqxlistbox').jqxListBox('getItem', args.index);
	                    var provinfo = 'Feature Vector: ' +feature[args.index]['featureVector']+'<br/>' +
	                    				'Source-Distance: '+feature[args.index]['sourceDistance'] + '<br/>' + 
	                    				'Type-Match: '+feature[args.index]['typeMatch'] + '<br/>'+ 
	                    				'Node-Distance: '+feature[args.index]['nodeDistance'] + '<br/>'+ 
	                    				'Property-Similarity: '+feature[args.index]['propertySimilarityMeasure']+ '<br/>' +
	                    				'Provenance: '+prov[args.index];
	                    $("#eventlog").html(provinfo);
	                    
	                });
					$("#button").jqxButton();
	                $("#button").click(function () {
	                    var item = $('#jqxlistbox').jqxListBox('getSelectedItem');
	                    if (item != null) {
	                        console.log(item.index);
	                        
	                        //Now we have to first set the box to have this value
	                        var newrow = parseInt(row,10)+1;
	                        var fieldset_id = "cell_"+(newrow)+"_"+(column);
	                        console.log(fieldset_id);
	                        $("#"+fieldset_id).val(item.label);
	                        $("#"+fieldset_id).focus();
	                        //Call-back to update feedback
	                        $.ajax({
	                        	url:'${pageContext.request.contextPath}/feedback',
	                        	dataType:'json',
	                        	data: {
	                        		'column':column,
	                        		'suggText': item.label
	                        		}
	                        	}).done(function(response){
	                        		$("#eventlog").html('Feedback received'+response);
	                        });
	                        
	                    };
	                });
				});
			}
			else {
				var rowbelow = parseInt(row, 10) +1;
				var markerid = 'cell_'+row+ "_" + 0;
				var markerText = $("#"+markerid).val();
				console.log("Marker id"+markerid+"|Marker text:"+markerText);
				
				var valueid = 'cell_'+row+"_"+column;
				var valueText = $("#"+valueid).val();
				console.log("Value id"+ valueid+ "|Value text:"+valueText);
				
				var newmarkerid = 'cell_'+rowbelow+"_"+0;
				var newmarkertext= $('#'+newmarkerid).val();
				console.log("New marker id"+ newmarkerid +"|New Marker text:"+newmarkertext);
				
				$.ajax({
					url:'${pageContext.request.contextPath}/getMVSugs',
					data: {
						'text':newmarkertext,	
						'column':column,
						'marker':markerText,
						'value':valueText,
					},
					dataType: "text"
				}).done(function(response) {
					console.log(response);
					var source = []
					var feature = []
					var prov = []
					var current, name, bit, curprov;
					var responseObjects = JSON.parse(response);
					//alert(responseObjects[0])
					
					for(var index = 0; index < responseObjects.length; index ++) {
						current = responseObjects[index];
						name = current['suggestionText'];
						bit = current['feature'];
						curprov = current['provInfo'];
						prov.push(curprov);
						feature.push(bit);
						source.push(name);
					}
					
					var selectedText;
					$("#jqxlistbox").jqxListBox({ source: source, width: '200px', height: '200px' });
					
					$('#jqxlistbox').bind('select', function (event) {
	                    var args = event.args;
	                    var item = $('#jqxlistbox').jqxListBox('getItem', args.index);
	                    var provinfo = 'Feature Vector: ' +feature[args.index]['featureVector']+'<br/>' + 
	                    'Source-Distance:'+feature[args.index]['sourceDistance'] + '<br/>' + 
        				'Type-Match: '+feature[args.index]['typeMatch'] + '<br/>'+ 
        				'Node-Distance: '+feature[args.index]['nodeDistance'] + '<br/>'+ 
        				'Property-Similarity: '+feature[args.index]['propertySimilarityMeasure'] + '<br/>' +
        				'Provenance: '+prov[args.index];
        				$("#eventlog").html(provinfo);
	                    
	                });
					$("#button").jqxButton();
	                $("#button").click(function () {
	                    var item = $('#jqxlistbox').jqxListBox('getSelectedItem');
	                    if (item != null) {
	                        console.log(item.index);
	                        
	                        //Now we have to first set the box to have this value
	                        var newrow = parseInt(row,10)+1;
	                        var fieldset_id = "cell_"+(newrow)+"_"+(column);
	                        console.log(fieldset_id);
	                        $("#"+fieldset_id).val(item.label);
	                        $("#"+fieldset_id).focus();
	                        //Call-back to update feedback
	                        $.ajax({
	                        	url:'${pageContext.request.contextPath}/feedback',
	                        	dataType:'json',
	                        	data: {
	                        		'column':column,
	                        		'suggText': item.label
	                        		}
	                        	}).done(function(response){
	                        		$("#eventlog").html('Feedback received'+response);
	                        });
	                        
	                    };
	                });
				});
			}
			
		}
	</script>
	
	

</body>
</html>
