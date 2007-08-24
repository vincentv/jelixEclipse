$KCODE = 'u'

require 'jbmObject'

class JbIndex  < JbmObject
  
  def initialize href, localDir
    super href, localDir
  end
  
  def parse
    page = super
    content = getContent(page)
    
    content = cleanPage(content)
    content = tTopic(content)
    content = tChapitre(content)
    content = tEndTopicXML(content)
    content = tTitreToc(content)
    
    save content, @saveBasePath + "/toc.xml"
  end
  
  private     
  def topicXML label, href = nil, tEnd = true
    tEnd = (tEnd)?"/>":">"
    href = (href.nil?)?'': "href=\"#{href}\""
    return   "<topic label=\"#{label}\" #{href} #{tEnd}"
  end
  
  def tEndTopicXML html
    rEnd = Regexp.new('<\/[o|u]l>\s*<\/[d|l]?iv?>', Regexp::MULTILINE)
    html = html.gsub(rEnd, '</topic>')
  end
  
  def tTopic(html)
    r = Regexp.new('<li class="\w+"><div class="\w+">[ |\w|\.|"|=|\:|\'|\-|\<|\>|\/|\(|\)]*</div>\s?<[u|o|\/]?li?>', Regexp::MULTILINE)
    html = html.gsub(r) do |t|      
      if t.include? "<a href="
        h = t.match(/<a href="([\w|\/|\.|\-]+)"[\s|=|"|\:|\w|\.|\-|\(|\)]*>(.*)<\/a>/)
        label = h[2] 
        href = h[1]
      else
        label = t.match(/<div class="\w+">\s?(.*)<\/div>/)[1]
        href = nil
      end
      
      t = topicXML label, href, (t.match(/<[u|o]?l>$/).nil?)
    end 
    return html
  end
  
  def tChapitre html
    rBegin = Regexp.new('<h2>[\w|\s]*<\/h2>\s*<div class="\w+">\s*<ol>', Regexp::MULTILINE)
    html = html.gsub(rBegin) do |t|
      label = t.match(/<h2>([\w|\s]*)<\/h2>/)[1]
      t= topicXML label, nil, false
    end
    
    #On recupere les information du chapitre information et on en fait un topic avec un lien
    rBegin = Regexp.new('<h2>[\w|\s]*<\/h2>\s?<div class="\w+">\s*<p>\s?[ |\w|\.|"|=|\:|\'|\-|\<|\>|\/|\(|\)]*\s?<\/p>\s*<\/div>', Regexp::MULTILINE)
    html = html.gsub(rBegin) do |t|
      label = t.match(/<h2>([\w|\s]*)<\/h2>/)[1]
      href = t.match(/<a href="([\w|\/|\.|-]+)"/)[1]
      t= topicXML label, href, true
    end
    
    rEnd = Regexp.new('<\/ol>\s*<ol>', Regexp::MULTILINE)
    html = html.gsub(rEnd, '')
    return html
  end
  
  def tTitreToc html
    rBegin = Regexp.new('<h1>[\w|\s]*<\/h1>\s*<div class="\w+">\s*<\/div>', Regexp::MULTILINE)
    html = html.gsub(rBegin) do |t|
      label = t.match(/<h1>([\w|\s]*)<\/h1>/)[1]
      t= "<toc label='#{label}'>"
    end
    
    rEnd = Regexp.new('<\/div>\s*$', Regexp::MULTILINE)
    html = html.gsub(rEnd, '</toc>')  
  end
end