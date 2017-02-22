$(document).ready(function () {
                            var next = 1;
                            $(".add-moref").click(function (e) {                                
                                e.preventDefault();
                                var addto = "#FILE" + next;
                                next = next + 1;
                                
                                var newIn = '<input type="file" value="#{festival.files[" + next + "]}" name="FILE' + next 
                                        + '" id="FILE' + next + '" class="form-control" accept="image/jpeg, image/jpg, image/png, video/mp4" />';

                                var newInput = $(newIn);

                                $(addto).after(newInput);
                                $(addto).after("<br/>");
                            });
                        });