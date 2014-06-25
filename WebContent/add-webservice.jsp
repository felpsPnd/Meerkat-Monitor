<%@include file="shared/validate-session.jsp" %>
<!DOCTYPE HTML>
<html>
	<head>
		<title>Meerkat-Monitor - Add Webservice</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<%@include file="shared/javascript-imports.txt" %>
		<%@include file="shared/css-imports.txt" %>
		<link rel="stylesheet" type="text/css" href="css/onoffswitch.css">
		
		<script type="text/javascript">
			$(document).ready(function(){
				<!-- handle the submit -->
				$("#settings").submit(function(){
					$("#msgbox").removeClass().addClass('info').html('Saving...<img src="images/loading.gif"> ').fadeIn(1000);
					this.timer = setTimeout(function () {
						<!-- Update all vars with user input -->
						var app_active = $("#active").is(":checked");
						var app_name = $("#name").val();
						var app_url = $("#url").val();
						var app_soapaction = $("#soapaction").val();
						var app_postxml = $("#postxml").val();
						var app_expectedxmlresponse = $("#expectedxmlresponse").val();
						var app_executeoffline = $("#executeoffline").val(); 
						var app_groups = $("#groups").val(); 
						
						$.ajax({
							url: '_add_webservice.jsp',
							data: 'active='+app_active+
								'&name='+app_name+
								'&url='+app_url+
								'&soapaction='+app_soapaction+
								'&postxml='+app_postxml+
								'&expectedxmlresponse='+app_expectedxmlresponse+
								'&executeoffline='+app_executeoffline+
								'&groups='+app_groups,
							type: 'post',
							success: function(msg){
									$("#msgbox").fadeTo(200,0.1,function(){
										$(this).html(msg).removeClass().fadeTo(900,1);
									});
							}
						});
					}, 200);

					return false;
				});
			});
		</script>
		
	</head>
	<body>
		<!-- Nav -->
		<nav id="nav">
			<ul>
				<%@include file="shared/menu-items.txt" %>
			</ul>
		</nav>
		
		<div class="wrapper wrapper-style2">
				<article class="5grid-layout" id="work">
					<header>
						<h2><strong>Meerkat-Monitor</strong></h2>
						<span>Add WebService</span>
					</header>
					
						<section class="box box-style1">
						
							<form name="settings" id="settings" action="" method="post">
								<!--  General Settings -->
								<section class="box box-style1">
									
									<!-- Active -->
									<div class="row">
										<div class="4u">
											<span>Active</span>
										</div>
										<div class="8u">
											<div class="onoffswitch">
							   	 				<input type="checkbox" name="appType" class="onoffswitch-checkbox" id="active" checked >
							    				<label class="onoffswitch-label" for="active">
							        				<div class="onoffswitch-inner"></div>
							        				<div class="onoffswitch-switch"></div>
							    				</label>
											</div>
										</div>
									</div>
																		
									<!-- Name -->
									<div class="row">
										<div class="4u">
											<span>Name</span>
										</div>
										<div class="4u">
											<input type="text" name="name" id="name" placeholder="Application Name" value=""/>
										</div>
									</div>
									
									<!-- URL -->
									<div class="row">
										<div class="4u">
											<span>URL</span>
										</div>
										<div class="4u">
											<input type="text" name="url" id="url" placeholder="Webservice URL" value=""/>
										</div>
									</div>
														
									<!-- SOAP ACTION -->
									<div class="row">
										<div class="4u">
											<span>SOAP Action</span>
										</div>
										<div class="4u">
											<input type="text" name="soapaction" id="soapaction" placeholder="SOAP Action (if any)" value=""/>
										</div>
									</div>
									
									<!-- POST XML -->
									<div class="row">
										<div class="4u">
											<span>Post XML</span>
										</div>
										<div class="4u">
											<textarea name="postxml" id="postxml" placeholder="XML to post" ></textarea>
										</div>
									</div>
									
									<!-- Expected XML Response -->
									<div class="row">
										<div class="4u">
											<span>Expected XML Response</span>
										</div>
										<div class="4u">
											<textarea name="expectedxmlresponse" id="expectedxmlresponse" placeholder="XML Response"></textarea>
										</div>
									</div>
									
									<!-- Execute on offline -->
									<div class="row">
										<div class="4u">
											<span>Execute on Offline</span>
										</div>
										<div class="4u">
											<input type="text" name="executeoffline" id="executeoffline" placeholder="Local cmd to execute when offline" value=""/>
										</div>
									</div>
									
									<!-- Groups -->
									<div class="row">
										<div class="4u">
											<span>Groups</span>
										</div>
										<div class="4u">
											<input type="text" name="groups" id="groups" placeholder="Groups (comma separated)" value=""/>
										</div>
									</div>
							
								</section>
							
								<input type="submit" class="button" value="Submit" />	
								<div id="msgbox"></div>
							</form>							
						</section>
						
				</article>
			</div>

			<!--  Footer -->
			<%@include file="shared/footer.txt" %>		

	</body>
</html>