$KCODE = 'u'

require 'rubygems'
require 'mechanize'

class JbmObject
  
  def initialize href, localDir
    @agent = WWW::Mechanize.new 
    @href = href
    @manuelBasePath = "html/manuel"
    @saveBasePath = localDir 
  end
  
  
  def parse href = nil
    return (href.nil?)?@agent.get(@href):@agent.get(href)    
  end
  
  protected
  def save page, path
    c = File.new(path, 'w')
    c.write(page)
    c.close
  end
  
  def fileName href
    filename = href.match(/\/articles\/manuel\/(.*)$/)[1]
    filename = filename.gsub(/\//, '_') 
    return filename + '.html'
  end
  
  def localPagePath href
    return (@manuelBasePath.nil?)?fileName(href):@manuelBasePath + '/' + fileName(href)
  end
  
  def cleanPage html
    html = cleanJavaScript(html)
    html = cleanLink(html)
    html = cleanComment(html)
    return html
  end
  
  def cleanJavaScript(html)
    #    html = html.gsub(/<script[\w|\ |=|"|\/|-|\.]+>.*<\/script>/, '')
    #    scriptRX = Regexp.new('<script[ |\w|\=|\"|\/|\-|\.]*>.*</script>', Regexp::MULTILINE)
    #    html = html.gsub(scriptRX, '')
    return html
  end
  
  def cleanLink(html)
    html = html.gsub(/<a name=\".*\"><\/a>/, "")
    
    html = html.gsub(/<a href=\".*<\/a>/) do |a|
      #on modifie la location des liens s'ils font partis de la documentation
      h = a.match(/href=\"(\/?articles\/manuel\/[\w|\/|-]+)\"/)
      unless h.nil?
        href = localPagePath(h[1])
        a = a.gsub(/href=\"[\w|\/|-]+\"/, "href=\"" + href + "\"")
      end
      
      #on efface les appels aux javascripts
      a = a.gsub(/onclick=\".+()"/, '')
      a = a.gsub(/onkeypress=\".+()"/, '')
    end
    
  end
  
  def cleanComment(html)
    return html.gsub(/<!-- .* -->/, '')  
  end
  
  def getContent page
    reg = Regexp.new("<div id=\"content-menu\">.*(<h1>.*)<div id=\"mainfooter\">", Regexp::MULTILINE)
    content = reg.match(page.content)
    return (content.nil?)?nil:content[1]
  end
  
end